package com.factset.sdk.streaming.examples;

import com.factset.sdk.streaming.client.WebsocketApiClient;
import com.factset.sdk.utils.authentication.ConfidentialClient;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test with server:
 * https://github.factset.com/eschmidt/websocket-playground/tree/main/packages/server-simple
 */
public class SimpleServerTest {

    private static final Logger logger = LoggerFactory.getLogger("main");

    public static void main(String[] args) throws Exception {

        ConfidentialClient authorizer = new ConfidentialClient(args[0]);

        WebsocketApiClient client = new WebsocketApiClient(
                WebsocketApiClient.Options.builder()
                        .url("http://localhost:8000/?noping")
                        .authorizer(authorizer)
                        .build()
        );

        client.connectAsync().join();

        client.request(new FooRequest(), BarResponse.class).thenAccept(response -> {
            logger.info("Got Response \\o/: {}", response);
        });

        client.subscribe(new StreamSubscriptionRequest(), StreamEvent.class, (e, t) -> {
            logger.info("Stream Event! {}", e, t);
        });

        Thread.sleep(30000);
        client.disconnect();

        // OkHttp needs about a minute to notice that no one is using it anymore
        // and then shuts down its threads as well, which lets the jvm exit
    }

    @Data
    @NoArgsConstructor
    public static class Meta {
        int id;
        String type;
        long timeout;

        public Meta(String type) {
            this.type = type;
        }
    }

    @Data
    public static class FooRequest {
        Meta meta = new Meta("FooRequest");
    }

    @Data
    public static class BarResponse {
        Meta meta;
        BarData data;

        @Data
        static class BarData {
            String bar;
        }
    }

    @Data
    public static class StreamSubscriptionRequest {
        Meta meta = new Meta("StreamSubscriptionRequest");
    }

    @Data
    public static class StreamEvent {
        Meta meta = new Meta("StreamEvent");
        EventData data;

        @Data
        public static class EventData {
            String time;
        }
    }

}
