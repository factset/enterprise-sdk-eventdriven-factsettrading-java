package com.factset.sdk.eventdriven.client;


import java.util.concurrent.CompletableFuture;

public interface ConnectableApiClient {
    CompletableFuture<? extends EventDrivenApiClient> connectAsync();

    /**
     * Disconnects the client.
     * The instance of the api client should be discarded after this call.
     */
    CompletableFuture<Void> disconnectAsync();
}
