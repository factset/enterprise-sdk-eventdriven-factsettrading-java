package com.factset.sdk.streaming.client;

@lombok.Data
class ConfigurationRequest {
    Meta meta = Meta.forClass(ConfigurationRequest.class);
    Data data = new Data();

    @lombok.Data
    public static class Data {
        long maximumIdleInterval;
    }
}
