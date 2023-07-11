package com.factset.sdk.eventdriven.client;

import java.util.concurrent.CompletableFuture;

public interface Subscription {
    /**
     * Cancels this subscription.
     * Sends an `UnsubscribeRequest` for the encapsulated subscription.
     *
     * @return a future that completes successfully when the server acknowledged the cancellation.
     */
    CompletableFuture<Void> cancel();
}
