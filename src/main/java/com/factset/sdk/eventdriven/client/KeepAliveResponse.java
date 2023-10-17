package com.factset.sdk.eventdriven.client;

import java.time.Instant;

@lombok.Data
class KeepAliveResponse {
    Meta meta = Meta.forClass(KeepAliveResponse.class);
    Data data = new Data();

    @lombok.Data
    public static class Data {
        Instant datetime = Instant.now();
    }

    public static KeepAliveResponse create(int id) {
        KeepAliveResponse response = new KeepAliveResponse();
        response.meta.id = id;

        return response;
    }
}
