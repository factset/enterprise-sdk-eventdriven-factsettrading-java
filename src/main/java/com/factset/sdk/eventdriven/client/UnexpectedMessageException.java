package com.factset.sdk.eventdriven.client;

public class UnexpectedMessageException extends ApiClientException {
    public UnexpectedMessageException(String message) {
        super(message);
    }
}
