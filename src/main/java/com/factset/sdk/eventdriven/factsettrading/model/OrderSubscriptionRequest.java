package com.factset.sdk.eventdriven.factsettrading.model;

import com.factset.sdk.eventdriven.model.Meta;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderSubscriptionRequest {
    Meta meta = Meta.forClass(OrderSubscriptionRequest.class);
    SubscriptionData data = new SubscriptionData();

    public OrderSubscriptionRequest(List<String> subscribeTo) {
        this.data.subscribe.addAll(subscribeTo);
    }

    @Data
    public static class SubscriptionData {
        List<String> subscribe = new ArrayList<>();
    }
}
