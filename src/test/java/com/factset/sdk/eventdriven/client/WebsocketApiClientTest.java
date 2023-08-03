package com.factset.sdk.eventdriven.client;

import com.factset.sdk.utils.authentication.ConfidentialClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class WebsocketApiClientTest {

    private static final ObjectMapper json = new ObjectMapper();
    private static final String wsUrl = "http://localhost:1234/";
    private static final String oauthToken = "dummy-token";
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final ConfidentialClient authorizer = mock(ConfidentialClient.class);
    private final OkHttpClient mockOkhttp = mock(OkHttpClient.class);
    private final WebSocket mockWebsocket = mock(WebSocket.class);
    private final CompletableFuture<WebSocketListener> websocketListenerFuture = new CompletableFuture<>();

    private final WebsocketApiClient client = new WebsocketApiClient(
            WebsocketApiClient.Options.builder()
                    .url(wsUrl)
                    .authorizer(authorizer)
                    .build(),
            mockOkhttp
    );

    /**
     * "sends" the given message to the websocket listener
     */
    private void sendMessageToClient(Object message) {
        websocketListenerFuture.thenAcceptAsync(listener -> {
            listener.onMessage(mockWebsocket, toJson(message));
        });
    }

    private static String toJson(Object o) {
        try {
            return json.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    private ErrorResponse buildErrorResponse() {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.getMeta().setId(1);
        Error error = Error.builder()
                .id("test-id")
                .code("test-code")
                .title("test-title")
                .build();
        List<Error> errorResponseArrayList = Arrays.asList(error);
        errorResponse.setErrors(errorResponseArrayList);

        return errorResponse;
    }

    @BeforeEach
    void setupMocks() throws Exception {

        // mock confidential client oauth stuff
        when(authorizer.getAccessToken()).thenReturn(oauthToken);

        // mock creation of a websocket
        when(mockOkhttp.newWebSocket(any(), any())).thenAnswer(invocation -> {
            WebSocketListener listener = invocation.getArgument(1, WebSocketListener.class);
            websocketListenerFuture.complete(listener);

            return mockWebsocket;
        });

        // mock call to listener.onOpen
        websocketListenerFuture.thenAcceptAsync(listener -> listener.onOpen(mockWebsocket, null));

        // mock reply to initial ConfigurationRequest
        when(mockWebsocket.send(contains("ConfigurationRequest"))).thenAnswer(invocation -> {
            String message = invocation.getArgument(0);

            ConfigurationResponse response = new ConfigurationResponse();
            response.data.sessionId = "";
            response.data.maximumIdleInterval = 1000;
            response.meta.id = extractIdFromJson(message);

            sendMessageToClient(response);

            return null;
        });
    }

    @AfterAll
    public static void cleanup() {
        scheduler.shutdownNow();
    }

    @Test
    @Timeout(2)
    public void opens_connection_properly() throws Exception {
        client.connectAsync().get(5, TimeUnit.SECONDS);

        Request expectedWSRequest = new Request.Builder()
                .url(wsUrl)
                .header("Authorization", "Bearer " + oauthToken)
                .header("User-Agent", "fds-sdk/java/eventdriven/null/null")
                .build();

        // unfortunately okhttp's Request doesn't implement equals,
        // so let's compare their string representations.
        verify(mockOkhttp).newWebSocket(argThat(r -> expectedWSRequest.toString().equals(r.toString())), any());

        verify(mockWebsocket).send(contains("ConfigurationRequest"));
    }

    @Test
    @Timeout(2)
    public void keep_alive_timeout_works() throws InterruptedException {
        when(mockWebsocket.send(contains("ConfigurationRequest"))).thenAnswer(invocation -> {
            String message = invocation.getArgument(0);

            ConfigurationResponse response = new ConfigurationResponse();
            response.data.sessionId = "";
            response.data.maximumIdleInterval = 50;
            response.meta.id = extractIdFromJson(message);

            sendMessageToClient(response);

            return null;
        });

        KeepAliveRequest keepAliveRequest = new KeepAliveRequest();

        client.connectAsync().join();

        verify(mockWebsocket, never()).close(1008, "Keep Alive Timeout");
        Thread.sleep(50);
        sendMessageToClient(keepAliveRequest);
        Thread.sleep(50);
        verify(mockWebsocket, never()).close(1008, "Keep Alive Timeout");
        Thread.sleep(100);
        verify(mockWebsocket, times(1)).close(1008, "Keep Alive Timeout");
    }

    @Test
    @Timeout(2)
    public void request_works() {
        ConfigurationRequest request = new ConfigurationRequest();
        request.data.maximumIdleInterval = 23456;

        ConfigurationResponse expectedResponse = new ConfigurationResponse();
        expectedResponse.data.sessionId = "";
        expectedResponse.data.maximumIdleInterval = 1000;

        Mockito.reset(mockWebsocket);
        when(mockWebsocket.send(contains("ConfigurationRequest"))).thenAnswer(invocation -> {
            String json = invocation.getArgument(0);
            expectedResponse.meta.id = extractIdFromJson(json);

            sendMessageToClient(expectedResponse);

            return null;
        });

        ConfigurationResponse response = client.connectAsync().thenCompose(c ->
                c.request(request, ConfigurationResponse.class)
        ).join();

        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    @Timeout(2)
    public void request_with_error_response_works() {
        ConfigurationRequest request = new ConfigurationRequest();
        request.data.maximumIdleInterval = 23456;

        ErrorResponse expectedResponse = buildErrorResponse();

        Mockito.reset(mockWebsocket);
        when(mockWebsocket.send(contains("ConfigurationRequest"))).thenAnswer(invocation -> {
            String json = invocation.getArgument(0);
            expectedResponse.meta.id = extractIdFromJson(json);

            sendMessageToClient(expectedResponse);

            return null;
        });

        try {
            client.connectAsync().thenCompose(c ->
                    c.request(request, ConfigurationResponse.class)
            ).join();

            fail("should not complete normally");
        } catch (CompletionException e) {
            assertInstanceOf(ErrorResponseException.class, e.getCause());
            ErrorResponseException exception = (ErrorResponseException) e.getCause();
            assertEquals(expectedResponse.getErrors(), exception.getErrorList());
        }
    }

    @Test
    @Timeout(2)
    public void request_with_timeout_works() throws Exception {
        ExampleRequest request = new ExampleRequest();
        request.meta.timeout = 50;

        try {
            client.connectAsync().thenCompose(c ->
                    // the actual response class does not matter here, as we should
                    // get the timeout after a while
                    c.request(request, ConfigurationResponse.class)
            ).get();

            fail("should not complete normally");
        } catch (ExecutionException e) {
            assertInstanceOf(RequestTimeoutException.class, e.getCause());
        }
    }


    @Test
    @Timeout(2)
    public void subscribe_happy_path() throws Exception {
        AtomicInteger eventId = new AtomicInteger(0);
        when(mockWebsocket.send(contains("ExampleSubscriptionRequest"))).thenAnswer(invocation -> {
            int id = extractIdFromJson(invocation.getArgument(0));
            eventId.set(id);

            // send immediate Ack
            AckResponse ack = new AckResponse();
            ack.meta.id = id;
            sendMessageToClient(ack);

            // send some events
            AtomicInteger count = new AtomicInteger(0);
            Runnable r = () -> {
                sendMessageToClient(ExampleSubscriptionEvent.create(id, count.incrementAndGet()));
            };

            scheduler.schedule(r, 50, TimeUnit.MILLISECONDS);
            scheduler.schedule(r, 70, TimeUnit.MILLISECONDS);
            scheduler.schedule(r, 90, TimeUnit.MILLISECONDS);

            return null;
        });

        client.connectAsync().join();

        List<ExampleSubscriptionEvent> events = Collections.synchronizedList(new ArrayList<>());
        AtomicReference<Throwable> exception = new AtomicReference<>();

        client.subscribe(
                new ExampleSubscriptionRequest(),
                ExampleSubscriptionEvent.class,
                (event, ex) -> {
                    if (ex != null) exception.set(ex);
                    events.add(event);
                }
        ).join();

        Thread.sleep(100);

        assertNull(exception.get());
        assertEquals(
                Arrays.asList(
                        ExampleSubscriptionEvent.create(eventId.get(), 1),
                        ExampleSubscriptionEvent.create(eventId.get(), 2),
                        ExampleSubscriptionEvent.create(eventId.get(), 3)
                ),
                events
        );
    }

    @Test
    @Timeout(2)
    public void subscribe_with_error_response() throws Exception {
        AtomicInteger eventId = new AtomicInteger(0);
        when(mockWebsocket.send(contains("ExampleSubscriptionRequest"))).thenAnswer(invocation -> {
            int id = extractIdFromJson(invocation.getArgument(0));
            eventId.set(id);

            // send immediate Ack
            AckResponse ack = new AckResponse();
            ack.meta.id = id;
            sendMessageToClient(ack);

            // send some events
            AtomicInteger count = new AtomicInteger(0);
            Runnable r = () -> {
                if (count.get() < 1) {
                    sendMessageToClient(ExampleSubscriptionEvent.create(id, count.incrementAndGet()));
                } else {
                    ErrorResponse errorResponse = buildErrorResponse();
                    errorResponse.meta.id = id;
                    sendMessageToClient(errorResponse);
                }

            };

            scheduler.schedule(r, 50, TimeUnit.MILLISECONDS);
            scheduler.schedule(r, 70, TimeUnit.MILLISECONDS);

            return null;
        });

        client.connectAsync().join();

        List<ExampleSubscriptionEvent> events = Collections.synchronizedList(new ArrayList<>());
        AtomicReference<Throwable> exception = new AtomicReference<>();

        client.subscribe(
                new ExampleSubscriptionRequest(),
                ExampleSubscriptionEvent.class,
                (event, ex) -> {
                    if (ex != null) exception.set(ex);
                    events.add(event);
                }
        ).join();

        Thread.sleep(100);

        assertInstanceOf(ErrorResponseException.class, exception.get());
        assertEquals(
                Arrays.asList(
                        ExampleSubscriptionEvent.create(eventId.get(), 1),
                        null
                ),
                events
        );
    }

    @Test
    public void disconnect_cleanup_subscriptions() {
        when(mockWebsocket.close(anyInt(), anyString())).then(invocation -> {
            websocketListenerFuture.thenAcceptAsync(listener -> {
                listener.onClosed(mockWebsocket, invocation.getArgument(0), invocation.getArgument(1));
            });

            return null;
        });

        when(mockWebsocket.send(contains("ExampleSubscriptionRequest"))).thenAnswer(invocation -> {
            AckResponse ack = new AckResponse();
            ack.meta.id = extractIdFromJson(invocation.getArgument(0));
            sendMessageToClient(ack);

            return null;
        });

        client.connectAsync().join();

        List<ExampleSubscriptionEvent> events = Collections.synchronizedList(new ArrayList<>());
        AtomicReference<Throwable> exception = new AtomicReference<>();

        client.subscribe(
                new ExampleSubscriptionRequest(),
                ExampleSubscriptionEvent.class,
                (event, ex) -> {
                    if (ex != null) exception.set(ex);
                    events.add(event);
                }
        ).join();

        client.disconnectAsync().join();

        assertInstanceOf(DisconnectException.class, exception.get());

        List<ExampleSubscriptionEvent> expectedList = new ArrayList<>();
        expectedList.add(null);

        assertEquals(expectedList, events);
    }

    @Test
    public void unsubscribe_works() {
        when(mockWebsocket.send(contains("ExampleSubscriptionRequest"))).thenAnswer(invocation -> {
            AckResponse ack = new AckResponse();
            ack.meta.id = extractIdFromJson(invocation.getArgument(0));
            sendMessageToClient(ack);

            return null;
        });

        client.connectAsync().join();
        Subscription subscription = client.subscribe(
                new ExampleSubscriptionRequest(),
                ExampleSubscriptionEvent.class, (e, t) -> {
                }
        ).join();

        subscription.cancel();

        verify(mockWebsocket).send(contains("UnsubscribeRequest"));
    }

    private int extractIdFromJson(String json) {
        Pattern regex = Pattern.compile("\"id\":([0-9]+)");
        Matcher matcher = regex.matcher(json);

        Assertions.assertTrue(matcher.find(), "could not extract id");

        return Integer.parseInt(matcher.group(1), 10);
    }

}

@Data
class ExampleRequest {
    Meta meta = Meta.forClass(ExampleRequest.class);
}

@Data
class ExampleSubscriptionRequest {
    Meta meta = Meta.forClass(this.getClass());
}

@Data
class ExampleSubscriptionEvent {
    Meta meta = Meta.forClass(this.getClass());
    EData data = new EData();

    @Data
    static class EData {
        int number;
    }

    static ExampleSubscriptionEvent create(int id, int n) {
        ExampleSubscriptionEvent e = new ExampleSubscriptionEvent();
        e.meta.id = id;
        e.data.number = n;
        return e;
    }
}