package com.factset.sdk.eventdriven.client;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public interface EventDrivenApiClient {

    /**
     * Sends a request and returns a future of the response.
     *
     * @throws InvalidRequestException if the request object isn't well-formed (e.g. follows the standard)
     */
    <TRequest, TResponse> CompletableFuture<TResponse> request(TRequest request, Class<TResponse> responseType);

    /**
     * Sends the request and prepares to receive subscription events of the given type.
     *
     * @return A future of a {@link Subscription}. Completes successfully once the server acknowledges the subscription
     *         request. It completes exceptionally in any other case.
     *
     * @throws InvalidRequestException if the request object isn't well-formed (e.g. follows the standard)
     */
    <TRequest, TResponse> CompletableFuture<Subscription> subscribe(TRequest request, Class<TResponse> responseType, BiConsumer<TResponse, Throwable> callback);

}

