package com.factset.sdk.eventdriven.client.model;

@lombok.Data
public class UnsubscribeRequest {
    Meta meta = Meta.forClass(UnsubscribeRequest.class);
    Data data = new Data();

    public UnsubscribeRequest(int subscriptionId) {
        this.data.subscriptionId = subscriptionId;
    }

    @lombok.Data
    static class Data {
        int subscriptionId;
    }
}
