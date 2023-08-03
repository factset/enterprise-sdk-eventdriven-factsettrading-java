package com.factset.sdk.eventdriven.client;

import com.factset.sdk.utils.authentication.OAuth2Client;
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
import java.util.function.Function;

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

    @Value
    private static class ResponseListener {
        String type;
        CompletableFuture<String> future;
    }

    @Value
    private static class SubscriptionListener {
        String type;
        BiConsumer<String, Throwable> handler;
    }

    @Data
    private static class IncomingMessage {
        Meta meta;
    }

    private final ObjectMapper json = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(new JavaTimeModule());

    private final Options options;
    private final OkHttpClient okhttp;

    private final EventIdGenerator eventIdGenerator = new EventIdGenerator();

    private final Map<Integer, ResponseListener> responseListeners = new ConcurrentHashMap<>();
    private final Map<Integer, SubscriptionListener> subscriptionListeners = new ConcurrentHashMap<>();

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
        request.data.maximumIdleInterval = options.maximumIdleInterval.toMillis();

        return request(request, ConfigurationResponse.class)
                .thenAccept(response -> {
                    long maximumIdleInterval = Math.min(request.data.maximumIdleInterval, response.getData().getMaximumIdleInterval());
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

        responseListeners.values().forEach(listener -> {
            listener.future.completeExceptionally(disconnectException);
        });

        subscriptionListeners.values().forEach(subscriptionListener -> {
            subscriptionListener.handler.accept(null, disconnectException);
        });

        responseListeners.clear();
        subscriptionListeners.clear();

        timeoutScheduler.shutdownNow();

        disconnectFuture.complete(null);
    }

    private void handleMessage(String message) {
        logger.debug("Received: {}", message);

        try {
            IncomingMessage msg = json.readValue(message, IncomingMessage.class);
            Meta meta = Objects.requireNonNull(msg.meta, "Meta must not be null.");

            if (handleSingleResponse(meta, message)) {
                return;
            }

            if (handleSubscriptionEvent(meta, message)) {
                return;
            }

            if (handleSystemMessages(meta, message)) {
                return;
            }

            logger.debug("Received Unhandled Message: {}", meta.type);

        } catch (JsonProcessingException | NullPointerException e) {
            logger.error("Could not parse incoming message: {} message={}", e, message);
        }
    }

    private boolean handleSystemMessages(Meta meta, String message) throws JsonProcessingException {
        switch (meta.type) {
            case "KeepAliveRequest":
                return handleKeepAliveRequest(message);
            default:
                return false;
        }
    }

    private boolean handleKeepAliveRequest(String message) throws JsonProcessingException {
        logger.debug("Handle KeepAliveRequest");

        KeepAliveRequest keepAliveRequest = json.readValue(message, KeepAliveRequest.class);
        KeepAliveResponse keepAliveResponse = KeepAliveResponse.create(keepAliveRequest.meta.getId());
        send(keepAliveResponse);

        connectionIsAlive = true;

        return true;
    }

    private boolean handleSingleResponse(Meta meta, String message) {
        // remove the listener right away if it is present
        ResponseListener listener = responseListeners.remove(meta.id);
        if (listener == null) {
            return false;
        }

        if (listener.type.equals(meta.type)) {
            listener.future.complete(message);
        } else if ("ErrorResponse".equals(meta.type)) {
            handleSingleResponseError(meta.id, listener, message);
        } else {
            String errorMessage = String.format(
                    "Unexpected message received. Expected: %s. Received: %s",
                    listener.type,
                    meta.type
            );
            listener.future.completeExceptionally(new UnexpectedMessageException(errorMessage));
        }

        return true;
    }

    private void handleSingleResponseError(int id, ResponseListener listener, String message) {
        logger.debug("Got an ErrorResponse for request with id: {}", id);
        try {
            ErrorResponse errorResponse = json.readValue(message, ErrorResponse.class);
            listener.future.completeExceptionally(new ErrorResponseException(errorResponse.getErrors()));
        } catch (JsonProcessingException e) {
            listener.future.completeExceptionally(new MalformedMessageException(e));
        }
    }

    private boolean handleSubscriptionEvent(Meta meta, String message) {
        SubscriptionListener listener = subscriptionListeners.get(meta.id);
        if (listener == null) {
            return false;
        }

        if (listener.type.equals(meta.type)) {
            listener.handler.accept(message, null);
        } else if ("ErrorResponse".equals(meta.type)) {
            handleSubscriptionEventError(meta.id, listener, message);
        } else {
            String errorMessage = String.format(
                    "Unexpected message received. Expected: %s. Received: %s",
                    listener.type,
                    meta.type
            );
            listener.handler.accept(null, new UnexpectedMessageException(errorMessage));
        }

        return true;
    }

    private void handleSubscriptionEventError(int id, SubscriptionListener listener, String message) {
        logger.debug("Got an ErrorResponse for subscription with id: {}", id);

        try {
            ErrorResponse errorResponse = json.readValue(message, ErrorResponse.class);
            listener.handler.accept(null, new ErrorResponseException(errorResponse.getErrors()));
        } catch (JsonProcessingException e) {
            listener.handler.accept(null, new MalformedMessageException(e));
        }

        logger.debug("Remove subscription listener for id: {}", id);
        subscriptionListeners.remove(id);

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
    public <TRequest, TResponse> CompletableFuture<TResponse> request(TRequest request, Class<TResponse> responseType) {
        ExtractedMeta meta = extractMeta(request);

        int id = getNextEventId();
        meta.setId(id);

        long timeout = meta.getTimeout();
        if (timeout <= 0) {
            timeout = options.defaultResponseTimeout.toMillis();
            meta.setTimeout(timeout);
        }

        CompletableFuture<String> future = timeoutFuture(id, timeout);

        try {
            responseListeners.put(id, new ResponseListener(responseType.getSimpleName(), future));
            send(request);
        } catch (Exception e) {
            future.completeExceptionally(new ApiClientException(e));
        }

        return future.thenApply(parseMessage(responseType));
    }

    private int getNextEventId() {
        int nextId;

        do {
            nextId = eventIdGenerator.getNextEventId();
        } while (responseListeners.containsKey(nextId) || subscriptionListeners.containsKey(nextId));

        return nextId;
    }

    private CompletableFuture<String> timeoutFuture(int id, long timeout) {
        CompletableFuture<String> future = new CompletableFuture<>();
        timeoutScheduler.schedule(() -> {
            if (!future.isDone()) {
                logger.debug("Timeout of request with id: {}", id);
                future.completeExceptionally(new RequestTimeoutException("Timeout of request with id: " + id));
                responseListeners.remove(id);
            }
        }, timeout, TimeUnit.MILLISECONDS);
        return future;
    }

    private void send(Object o) throws JsonProcessingException {
        String json = this.json.writeValueAsString(o);
        logger.debug("Sending data: json={}", json);
        websocket.send(json);
    }

    private <TResponse> Function<String, TResponse> parseMessage(Class<TResponse> responseType) {
        return text -> {
            try {
                return json.readValue(text, responseType);
            } catch (JsonProcessingException e) {
                // the CompletableFuture machinery expects a CompletionException
                throw new CompletionException(new MalformedMessageException(e));
            }
        };
    }

    @Override
    public <TRequest, TResponse> CompletableFuture<Subscription> subscribe(TRequest request, Class<TResponse> responseType, BiConsumer<TResponse, Throwable> callback) {
        BiConsumer<String, Throwable> listener = (s, t) -> {
            if (t != null) {
                callback.accept(null, t);
                return;
            }

            try {
                callback.accept(json.readValue(s, responseType), null);
            } catch (JsonProcessingException e) {
                callback.accept(null, new MalformedMessageException(e));
            } catch (Exception e) {
                logger.warn("Exception while calling subscription callback", e);
            }
        };

        return request(request, AckResponse.class).thenApply(ackResponse -> {
            int id = ackResponse.meta.id;
            subscriptionListeners.put(id, new SubscriptionListener(responseType.getSimpleName(), listener));
            return () -> cancelSubscription(id);
        });
    }

    private CompletableFuture<Void> cancelSubscription(int id) {
        logger.debug("cancel subscription: id={}", id);

        // send the unsubscribe request
        return request(new UnsubscribeRequest(id), AckResponse.class).thenAccept(ackResponse -> {
            subscriptionListeners.remove(id);
        });
    }

}