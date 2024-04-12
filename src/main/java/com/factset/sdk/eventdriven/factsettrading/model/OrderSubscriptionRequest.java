package com.factset.sdk.eventdriven.factsettrading.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class OrderSubscriptionRequest {
    Meta meta = new Meta("OrderSubscriptionRequest");
    SubscriptionData data = new SubscriptionData();

    public OrderSubscriptionRequest(Subscribe subscribeTo) {
        this.data.subscribe = subscribeTo;
    }

    @Data
    public static class SubscriptionData {
        Subscribe subscribe = Subscribe.builder().build();
    }

    @Data
    @Builder
    public static class Subscribe{
        boolean inboundOrders;
        boolean parentOrders;
        boolean childOrders;
        boolean inboundMessages;
        boolean childMessages;
    }
}
