package com.factset.sdk.streaming.client;

@lombok.Data
class UnsubscribeRequest {
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
