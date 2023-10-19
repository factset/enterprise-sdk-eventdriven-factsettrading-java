package com.factset.sdk.eventdriven.client;

import com.factset.sdk.utils.authentication.OAuth2Client;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Value;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

import static com.factset.sdk.eventdriven.client.ExtractedMeta.extractMeta;

public class WebsocketApiClient implements EventDrivenApiClient, ConnectableApiClient {

    private final static Logger logger = LoggerFactory.getLogger(WebsocketApiClient.class);
    private final static String implementationVersion = WebsocketApiClient.class.getPackage().getImplementationVersion();
    private final static String implementationTitle = WebsocketApiClient.class.getPackage().getImplementationTitle();
    private final static String userAgent = "fds-sdk/java/eventdriven/" + implementationTitle + "/" + implementationVersion;

    @Value
    @Builder
    public static class Options {
        /**
         * The url of the remote websocket.
         */
        @NonNull String url;

        @NonNull OAuth2Client authorizer;

        @Builder.Default
        int numberConnectRetries = 5;

        /**
         * The maximum interval between messages from the server, after which the client
         * considers the connection stale.
         */
        @Builder.Default
        Duration maximumIdleInterval = Duration.ofSeconds(60);

        /**
         * The default timeout to wait for responses.
         * Can be overwritten per request.
         */
        @Builder.Default
        Duration defaultResponseTimeout = Duration.ofSeconds(5);
    }

    @FunctionalInterface
    private interface MessageListener extends BiConsumer<IncomingMessage, Throwable> {
    }

    @Data
    private static class IncomingMessage implements Message {
        Meta meta;

        @JsonIgnore
        private String json;

        @JsonIgnore
        private ObjectMapper jsonParser;

        public <T> T parseAs(Class<T> messageType) throws MalformedMessageException, UnexpectedMessageException {
            if (!messageType.getSimpleName().equals(meta.getType())) {
                throw new UnexpectedMessageException(messageType.getSimpleName(), this);
            }

            try {
                return jsonParser.readValue(json, messageType);
            } catch (JsonProcessingException e) {
                throw new MalformedMessageException(e);
            }
        }

        @Override
        public String getType() {
            return meta.getType();
        }
    }

    private final ObjectMapper jsonParser = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(new JavaTimeModule());

    private final Options options;
    private final OkHttpClient okhttp;

    private final EventIdGenerator eventIdGenerator = new EventIdGenerator();

    private final Map<Integer, MessageListener> messageListeners = new ConcurrentHashMap<>();

    private WebSocket websocket;

    private final ScheduledExecutorService timeoutScheduler;

    private volatile boolean connectionIsAlive = true;

    private final CompletableFuture<Void> disconnectFuture;

    public WebsocketApiClient(Options options) {
        this(options, new OkHttpClient());
    }

    public WebsocketApiClient(Options options, OkHttpClient okhttp) {
        this.options = options;
        this.okhttp = okhttp;
        disconnectFuture = new CompletableFuture<>();

        timeoutScheduler = Executors.newSingleThreadScheduledExecutor((r) -> {
            Thread t = new Thread(r, "sdk-timeouts");
            t.setDaemon(true);
            return t;
        });
    }

    @Override
    public CompletableFuture<WebsocketApiClient> connectAsync() {
        return ExponentialBackoffRetry.withRetries(this::openNewWebsocket, options.numberConnectRetries, timeoutScheduler)
                .thenAccept(webSocket -> this.websocket = webSocket)
                .thenCompose(ignored -> configureConnection())
                .thenApply(ignored -> this);
    }

    private CompletableFuture<WebSocket> openNewWebsocket() {
        return getAccessToken()
                .thenApply(this::prepareWebsocketRequest)
                .thenCompose(this::openWebsocket);
    }

    private CompletableFuture<Void> configureConnection() {
        ConfigurationRequest request = new ConfigurationRequest();
        request.getData().setMaximumIdleInterval(options.maximumIdleInterval.toMillis());

        return request(request)
                .thenAccept(message -> {
                    ConfigurationResponse configurationResponse = message.parseAs(ConfigurationResponse.class);
                    long maximumIdleInterval = Math.min(request.getData().getMaximumIdleInterval(), configurationResponse.getData().getMaximumIdleInterval());
                    setupKeepAliveScheduler(maximumIdleInterval);
                });
    }

    private void setupKeepAliveScheduler(long maximumIdleInterval) {
        timeoutScheduler.scheduleAtFixedRate(() -> {
            if (!connectionIsAlive) {
                logger.debug("Keep alive timeout hit");
                disconnect(WebsocketCloseCode.POLICY_VIOLATION, "Keep Alive Timeout");
            }
            connectionIsAlive = false;
        }, maximumIdleInterval, maximumIdleInterval, TimeUnit.MILLISECONDS);
    }

    private CompletableFuture<String> getAccessToken() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return options.authorizer.getAccessToken();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    private Request prepareWebsocketRequest(String accessToken) {
        return new Request.Builder()
                .url(options.url)
                .header("Authorization", "Bearer " + accessToken)
                .header("User-Agent", userAgent)
                .header("Sec-WebSocket-Protocol", "v1.json.factset.com")
                .build();
    }

    private CompletableFuture<WebSocket> openWebsocket(Request wsRequest) {
        CompletableFuture<WebSocket> openWebSocketFuture = new CompletableFuture<>();

        WebSocketListener listener = new WebSocketListener() {
            @Override
            @SuppressWarnings("NullableProblems")
            public void onOpen(WebSocket webSocket, Response response) {
                logger.debug("websocket opened");
                openWebSocketFuture.complete(webSocket);
            }

            @Override
            @SuppressWarnings("NullableProblems")
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                logger.error("websocket failure: {}. response={}", t, response);

                if (openWebSocketFuture.isDone()) {
                    // cleanup resource, if the websocket is in a failure state and was opened before
                    cleanupResources(WebsocketCloseCode.PROTOCOL_ERROR, t.toString(), t);
                } else {
                    // if an error occurs when opening the websocket we hand back the exception
                    openWebSocketFuture.completeExceptionally(t);
                }
            }

            @Override
            @SuppressWarnings("NullableProblems")
            public void onClosed(WebSocket webSocket, int code, String reason) {
                cleanupResources(code, reason, null);
                disconnectFuture.complete(null);

                logger.info("websocket closed: code={} reason={}", code, reason);
            }

            @Override
            @SuppressWarnings("NullableProblems")
            public void onMessage(WebSocket webSocket, String text) {
                handleMessage(text);
            }
        };

        okhttp.newWebSocket(wsRequest, listener);

        return openWebSocketFuture;
    }

    private void cleanupResources(int code, String reason, Throwable t) {
        logger.debug("cleanup called: error={} code={} reason={}", t, code, reason);

        DisconnectException disconnectException;
        if (t != null) {
            disconnectException = new DisconnectException(code, reason, t);
        } else {
            disconnectException = new DisconnectException(code, reason);
        }

        messageListeners.values().forEach(messageListener -> {
            messageListener.accept(null, disconnectException);
        });

        messageListeners.clear();

        timeoutScheduler.shutdownNow();

        disconnectFuture.complete(null);
    }

    private void handleMessage(String message) {
        logger.debug("Received: {}", message);

        try {
            IncomingMessage msg = jsonParser.readValue(message, IncomingMessage.class);
            msg.json = message;
            msg.jsonParser = jsonParser;

            Meta meta = Objects.requireNonNull(msg.meta, "Meta must not be null.");

            if (handleApiMessage(msg)) {
                return;
            }

            if (handleSystemMessages(msg)) {
                return;
            }

            logger.debug("Received Unhandled Message: {}", meta.type);

        } catch (JsonProcessingException | NullPointerException e) {
            logger.error("Could not parse incoming message: {} message={}", e, message);
        }
    }

    private boolean handleSystemMessages(IncomingMessage message) throws JsonProcessingException {
        switch (message.meta.type) {
            case "KeepAliveRequest":
                return handleKeepAliveRequest(message.json);
            default:
                return false;
        }
    }

    private boolean handleKeepAliveRequest(String message) throws JsonProcessingException {
        logger.debug("Handle KeepAliveRequest");

        KeepAliveRequest keepAliveRequest = jsonParser.readValue(message, KeepAliveRequest.class);
        KeepAliveResponse keepAliveResponse = KeepAliveResponse.create(keepAliveRequest.meta.getId());
        send(keepAliveResponse);

        connectionIsAlive = true;

        return true;
    }

    private boolean handleApiMessage(IncomingMessage message) {
        MessageListener listener = messageListeners.get(message.meta.id);
        if (listener == null) {
            return false;
        }

        if ("ErrorResponse".equals(message.meta.type)) {
            handleErrorResponse(message, listener);
        } else {
            listener.accept(message, null);
        }

        return true;
    }

    private void handleErrorResponse(IncomingMessage message, MessageListener listener) {
        logger.debug("Got an ErrorResponse for subscription with id: {}", message.meta.getId());

        try {
            ErrorResponse errorResponse = jsonParser.readValue(message.json, ErrorResponse.class);
            listener.accept(null, new ErrorResponseException(errorResponse.getErrors()));
        } catch (JsonProcessingException e) {
            listener.accept(null, new MalformedMessageException(e));
        }

        logger.debug("Remove subscription listener for id: {}", message.meta.getId());
        messageListeners.remove(message.meta.getId());
    }

    @Override
    public CompletableFuture<Void> disconnectAsync() {
        return disconnect(WebsocketCloseCode.NORMAL_CLOSURE, "disconnect");
    }

    private CompletableFuture<Void> disconnect(int code, String reason) {
        logger.debug("disconnect called: code={} reason={}", code, reason);

        websocket.close(code, reason);
        websocket = null;

        return disconnectFuture;
    }

    @Override
    public CompletableFuture<Message> request(Object request) {
        ExtractedMeta meta = extractMeta(request);

        int id = getNextEventId();
        meta.setId(id);

        long timeout = meta.getTimeout();
        if (timeout <= 0) {
            timeout = options.defaultResponseTimeout.toMillis();
            meta.setTimeout(timeout);
        }

        CompletableFuture<Message> future = timeoutFuture(id, timeout);

        messageListeners.put(id,
                (msg, err) -> {
                    if (msg != null) {
                        future.complete(msg);
                    } else {
                        future.completeExceptionally(err);
                    }
                }
        );

        try {
            send(request);
        } catch (Exception e) {
            future.completeExceptionally(new ApiClientException(e));
        }

        return future
                .whenComplete((msg, err) -> messageListeners.remove(id));
    }

    private int getNextEventId() {
        int nextId;

        do {
            nextId = eventIdGenerator.getNextEventId();
        } while (messageListeners.containsKey(nextId));

        return nextId;
    }

    private <T> CompletableFuture<T> timeoutFuture(int id, long timeout) {
        CompletableFuture<T> future = new CompletableFuture<>();
        timeoutScheduler.schedule(() -> {
            if (!future.isDone()) {
                logger.debug("Timeout of request with id: {}", id);
                future.completeExceptionally(new RequestTimeoutException("Timeout of request with id: " + id));
            }
        }, timeout, TimeUnit.MILLISECONDS);
        return future;
    }

    private void send(Object o) throws JsonProcessingException {
        String json = this.jsonParser.writeValueAsString(o);
        logger.debug("Sending data: json={}", json);
        websocket.send(json);
    }

    @Override
    public CompletableFuture<Subscription> subscribe(Object request, BiConsumer<Message, Throwable> callback) {
        MessageListener listener = (msg, t) -> {
            if (t != null) {
                callback.accept(null, t);
                return;
            }

            try {
                callback.accept(msg, null);
            } catch (Exception e) {
                logger.warn("Exception while calling subscription callback", e);
            }
        };

        return request(request).thenApply(message -> {
            AckResponse ackResponse = message.parseAs(AckResponse.class);
            int id = ackResponse.getMeta().getId();
            messageListeners.put(id, listener);
            return () -> cancelSubscription(id);
        });
    }

    private CompletableFuture<Void> cancelSubscription(int id) {
        logger.debug("cancel subscription: id={}", id);

        if (messageListeners.containsKey(id)) {
            // send the unsubscribe request
            return request(new UnsubscribeRequest(id)).thenAccept(message -> {
                messageListeners.remove(id);
            });
        } else {
            logger.warn("unknown subscription: id={}", id);
            return CompletableFuture.completedFuture(null);
        }
    }
}