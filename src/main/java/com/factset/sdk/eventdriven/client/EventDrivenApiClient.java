package com.factset.sdk.eventdriven.client;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public interface EventDrivenApiClient {

    /**
     * Sends a request and returns a future of the response.
     *
     * @param request request to be made
     * @return CompletableFuture
     * @param <TRequest> type of the request
     *
     * @throws InvalidRequestException if the request object isn't well-formed (e.g. follows the standard)
     */
    <TRequest> CompletableFuture<Message> request(TRequest request);

    /**
     * Sends the request and prepares to receive subscription events of the given type.
     *
     * @param request request to be made
     * @param callback function to be called with the response
     * @return A future of a {@link Subscription}. Completes successfully once the server acknowledges the subscription
     *         request. It completes exceptionally in any other case.
     * @param <TRequest> type of the request
     *
     * @throws InvalidRequestException if the request object isn't well-formed (e.g. follows the standard)
     */
    <TRequest> CompletableFuture<Subscription> subscribe(TRequest request, BiConsumer<Message, Throwable> callback);

}

