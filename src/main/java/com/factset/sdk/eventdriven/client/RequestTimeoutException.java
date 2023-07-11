package com.factset.sdk.eventdriven.client;

public class RequestTimeoutException extends ApiClientException {

    public RequestTimeoutException(String message) {
        super(message);
    }
    
}
