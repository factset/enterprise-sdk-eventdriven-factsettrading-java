package com.factset.sdk.eventdriven.client.model;

@lombok.Data
public class KeepAliveRequest {
    Meta meta = Meta.forClass(KeepAliveRequest.class);
    Data data = new Data();

    @lombok.Data
    public static class Data {
        String datetime;
    }
}
