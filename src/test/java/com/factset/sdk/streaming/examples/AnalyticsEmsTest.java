package com.factset.sdk.streaming.examples;

import com.factset.sdk.streaming.client.Subscription;
import com.factset.sdk.streaming.client.WebsocketApiClient;
import com.factset.sdk.streaming.factsettrading.OrderUpdateApi;
import com.factset.sdk.utils.authentication.ConfidentialClient;
import com.factset.sdk.utils.authentication.OAuth2Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;

public class AnalyticsEmsTest {

    private static final Logger logger = LoggerFactory.getLogger("main");

    public static void main(String[] args) throws Exception {

        OAuth2Client authorizer = args.length > 0 ? new ConfidentialClient(args[0]) : () -> "";

        WebsocketApiClient client = new WebsocketApiClient(
                WebsocketApiClient.Options.builder()
                        .url("http://analytics-api-ems-streaming-inhouse.factset.io/streaming/trading/ems/v1")
                        .authorizer(authorizer)
                        .build()
        );

        // Some Notes:
        //
        // CompletableFuture is conceptually similar to JavaScript Promises,
        // just with a tad more flavors of the 'then' and 'catch' methods.
        //
        // CompletableFuture.join blocks the current thread, until the future completes,
        // then returns its result or throws any errors that occurred.
        // Another version of .join() is .get().
        //
        // In general you'd want to avoid using those two methods and instead chain handlers
        // to the future.
        //
        // A Promise resolves or rejects.
        // A CompletableFuture completes successfully or exceptionally.

        OrderUpdateApi api = client.connectAsync()
                .thenApply(OrderUpdateApi::new)
                .join();

        // FYI - you may want to set the loglevel to INFO in resources/simplelogger.properties
        try {
            Subscription subscription = api.subscribeOrderUpdates((update, t) -> {
                if (t != null) {
                    logger.warn("something went wrong: {}", t.getMessage());
                } else {
                    logger.info("order update: {}", update);
                }
            }).join();

            Thread.sleep(10000);

            // I suppose this isn't really necessary for your use-case
            subscription.cancel();
        } catch (CompletionException | CancellationException e) {
            logger.error(e.toString());
        }
        Thread.sleep(10000);
        // note: the underlying OkHttp instance will keep the JVM from running for about another minute
        // after this call.
        client.disconnect().join();
    }

}
