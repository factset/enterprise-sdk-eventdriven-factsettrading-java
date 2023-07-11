package com.factset.sdk.eventdriven.factsettrading;

import com.factset.sdk.eventdriven.client.Subscription;
import com.factset.sdk.eventdriven.client.WebsocketApiClient;
import com.factset.sdk.eventdriven.factsettrading.model.OrderSubscriptionRequest;
import com.factset.sdk.eventdriven.factsettrading.model.OrderUpdateEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class OrderUpdateApi {
    private final WebsocketApiClient client;

    public OrderUpdateApi(WebsocketApiClient client) {
        this.client = client;
    }

    public CompletableFuture<Subscription> subscribeOrderUpdates(BiConsumer<OrderUpdateEvent, Throwable> callback) {
        List<String> subscribeList = Arrays.asList("orderupdates");
        OrderSubscriptionRequest request = new OrderSubscriptionRequest(subscribeList);
        return client.subscribe(request, OrderUpdateEvent.class, callback);
    }
}
