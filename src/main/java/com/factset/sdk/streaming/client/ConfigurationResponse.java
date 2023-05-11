package com.factset.sdk.streaming.client;

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
