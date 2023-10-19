package com.factset.sdk.eventdriven.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class KeepAliveResponseTest {

    private static final ObjectMapper json = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(new JavaTimeModule());

    @Test
    void fromJsonWithZ() throws JsonProcessingException {
        String instantExpected = "2022-03-14T09:33:52.123Z";
        Instant instantDateTime = Instant.parse(instantExpected);
        String jsonResponse = "{\"meta\":{\"id\":1,\"type\":\"KeepAliveResponse\",\"timeout\":0},\"data\":{\"datetime\":\"2022-03-14T09:33:52.123Z\"}}";

        KeepAliveResponse response = json.readValue(jsonResponse, KeepAliveResponse.class);
        Assertions.assertEquals(instantDateTime, response.data.datetime);
    }

    @Test
    void fromJsonWith0000() throws JsonProcessingException {
        String instantExpected = "2022-03-14T09:33:52.123Z";
        Instant instantDateTime = Instant.parse(instantExpected);
        String jsonResponse = "{\"meta\":{\"id\":1,\"type\":\"KeepAliveResponse\",\"timeout\":0},\"data\":{\"datetime\":\"2022-03-14T09:33:52.123+00:00\"}}";

        KeepAliveResponse response = json.readValue(jsonResponse, KeepAliveResponse.class);
        Assertions.assertEquals(instantDateTime, response.data.datetime);
    }

    @Test
    void toJson() throws JsonProcessingException {
        KeepAliveResponse response = new KeepAliveResponse();
        response.data.datetime = Instant.parse("2022-03-14T09:33:52.123Z");
        response.meta.id = 1;

        Assertions.assertEquals("{\"meta\":{\"id\":1,\"type\":\"KeepAliveResponse\",\"timeout\":0},\"data\":{\"datetime\":\"2022-03-14T09:33:52.123Z\"}}", json.writeValueAsString(response));
    }
}