package com.factset.sdk.eventdriven.client;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public interface EventDrivenApiClient {

    /**
     * Sends a request and returns a future of the response.
     *
     * @param request request to be made
     * @return CompletableFuture
     * @throws InvalidRequestException if the request object isn't well-formed (e.g. follows the standard)
     */
    CompletableFuture<Message> request(Object request);

    default <T> CompletableFuture<T> request(Object request, Class<T> expectedMessage) {
        return request(request).thenApply(message -> message.parseAs(expectedMessage));
    }

    /**
     * Sends the request and prepares to receive subscription events of the given type.
     *
     * @param request  request to be made
     * @param callback function to be called with the response
     * @return A future of a {@link Subscription}. Completes successfully once the server acknowledges the subscription
     * request. It completes exceptionally in any other case.
     * @throws InvalidRequestException if the request object isn't well-formed (e.g. doesn't follow the standard)
     */
    CompletableFuture<Subscription> subscribe(Object request, BiConsumer<Message, Throwable> callback);

    default <T> CompletableFuture<Subscription> subscribe(Object request, Class<T> expectedMessage, BiConsumer<T, Throwable> callback) {
        return subscribe(request, (msg, t) -> {
            if (msg != null) {
                try {
                    callback.accept(msg.parseAs(expectedMessage), null);
                } catch (Throwable ex) {
                    callback.accept(null, ex);
                }
            } else {
                callback.accept(null, t);
            }
        });
    }

}

