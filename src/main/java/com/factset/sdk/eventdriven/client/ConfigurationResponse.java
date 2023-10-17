package com.factset.sdk.eventdriven.client;

@lombok.Data
class ConfigurationResponse {
    Meta meta = Meta.forClass(ConfigurationResponse.class);
    Data data = new Data();

    @lombok.Data
    public static class Data {
        int maximumIdleInterval;
        String sessionId;
    }
}
