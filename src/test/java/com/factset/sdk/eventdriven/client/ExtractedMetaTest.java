package com.factset.sdk.eventdriven.client;

import com.factset.sdk.eventdriven.client.model.ConfigurationRequest;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.factset.sdk.eventdriven.client.ExtractedMeta.extractMeta;

class ExtractedMetaTest {
    private final ConfigurationRequest request = new ConfigurationRequest();

    @Test
    void getTimeout() {
        request.getMeta().setTimeout(12);

        ExtractedMeta extractedMeta = extractMeta(request);

        Assertions.assertEquals(12, extractedMeta.getTimeout());
    }

    @Test
    void setTimeout() {
        ExtractedMeta extractedMeta = extractMeta(request);

        extractedMeta.setTimeout(12);

        Assertions.assertEquals(12, request.getMeta().getTimeout());
    }

    @Test
    void setId() {
        ExtractedMeta extractedMeta = extractMeta(request);

        extractedMeta.setId(12);

        Assertions.assertEquals(12, request.getMeta().getId());
    }

    @Test
    void throws_on_wrongly_shaped_object() {
        Assertions.assertThrows(InvalidRequestException.class, () -> extractMeta(new HasNoMeta()));
    }

    @Data
    public static class HasNoMeta {
        String foo;
    }
}