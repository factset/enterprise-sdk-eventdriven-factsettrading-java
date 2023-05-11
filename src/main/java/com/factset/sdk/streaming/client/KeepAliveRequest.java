package com.factset.sdk.streaming.client;

@lombok.Data
class KeepAliveRequest {
    Meta meta = Meta.forClass(KeepAliveRequest.class);
    Data data = new Data();

    @lombok.Data
    public static class Data {
        String datetime;
    }
}
