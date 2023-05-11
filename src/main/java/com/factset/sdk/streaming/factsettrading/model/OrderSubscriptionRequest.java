package com.factset.sdk.streaming.factsettrading.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderSubscriptionRequest {
    Meta meta = new Meta("OrderSubscriptionRequest");
    SubscriptionData data = new SubscriptionData();

    public OrderSubscriptionRequest(List<String> subscribeTo) {
        this.data.subscribe.addAll(subscribeTo);
    }

    @Data
    public static class SubscriptionData {
        List<String> subscribe = new ArrayList<>();
    }
}
