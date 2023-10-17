package com.factset.sdk.eventdriven.client;

@lombok.Data
class ConfigurationRequest {
    Meta meta = Meta.forClass(ConfigurationRequest.class);
    Data data = new Data();

    @lombok.Data
    public static class Data {
        long maximumIdleInterval;
    }
}
