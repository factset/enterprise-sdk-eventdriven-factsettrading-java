package com.factset.sdk.eventdriven.model;

@lombok.Data
public class ConfigurationRequest {
    Meta meta = Meta.forClass(ConfigurationRequest.class);
    Data data = new Data();

    @lombok.Data
    public static class Data {
        long maximumIdleInterval;
    }
}
