package com.factset.sdk.eventdriven.client;

import com.factset.sdk.eventdriven.client.Error;
import com.factset.sdk.eventdriven.client.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ErrorResponseTest {

    private static final ObjectMapper json = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(new JavaTimeModule());


    @Test
    void fromJsonWithNoErrors() throws JsonProcessingException {
        String jsonResponse = "{\"meta\":{\"id\":1,\"type\":\"ErrorResponse\",\"timeout\":0},\"errors\":[]}";
        ErrorResponse response = json.readValue(jsonResponse, ErrorResponse.class);
        Assertions.assertEquals(0, response.getErrors().size());
    }

    @Test
    void fromJsonWithMandatoryPropertiesOnly() throws JsonProcessingException {
        String jsonResponse = "{\"meta\":{\"id\":1,\"type\":\"ErrorResponse\",\"timeout\":0},\"errors\":[{\"id\":\"test-id\",\"code\":\"test-code\",\"title\":\"test-title\"}]}";
        ErrorResponse response = json.readValue(jsonResponse, ErrorResponse.class);
        Assertions.assertEquals(1, response.getErrors().size());

        Error error = new Error();
        error.setId("test-id");
        error.setCode("test-code");
        error.setTitle("test-title");
        List<Error> errorResponseArrayList = new ArrayList<>();
        errorResponseArrayList.add(error);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.getMeta().setId(1);
        errorResponse.setErrors(errorResponseArrayList);

        Assertions.assertEquals(errorResponse, response);

    }

    @Test
    void fromJsonWithFullError() throws JsonProcessingException {
        String jsonResponse = "{\"meta\":{\"id\":1,\"type\":\"ErrorResponse\",\"timeout\":0},\"errors\":[{\"id\":\"test-id\",\"code\":\"test-code\",\"title\":\"test-title\",\"links\":{\"about\":\"test-about\"},\"details\":\"test-details\",\"source\":{\"pointer\":\"test-pointer\",\"parameter\":\"test-parameter\"}}]}";
        ErrorResponse response = json.readValue(jsonResponse, ErrorResponse.class);
        Assertions.assertEquals(1, response.getErrors().size());

        Error error = new Error();
        error.setId("test-id");
        error.setCode("test-code");
        error.setTitle("test-title");

        Error.Links links = Error.Links.builder()
                .about("test-about")
                .build();
        error.setLinks(links);
        error.setDetails("test-details");

        Error.Source source = Error.Source.builder()
                .parameter("test-parameter")
                .pointer("test-pointer")
                .build();
        error.setSource(source);

        List<Error> errorResponseArrayList = new ArrayList<>();
        errorResponseArrayList.add(error);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.getMeta().setId(1);
        errorResponse.setErrors(errorResponseArrayList);

        Assertions.assertEquals(errorResponse, response);
    }

}
