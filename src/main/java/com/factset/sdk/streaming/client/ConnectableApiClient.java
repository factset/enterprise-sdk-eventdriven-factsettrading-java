package com.factset.sdk.streaming.client;


import java.util.concurrent.CompletableFuture;

public interface ConnectableApiClient {
    CompletableFuture<? extends StreamingApiClient> connectAsync();

    /**
     * Disconnects the client.
     * The instance of the api client should be discarded after this call.
     */
    CompletableFuture<Void> disconnectAsync();
}
