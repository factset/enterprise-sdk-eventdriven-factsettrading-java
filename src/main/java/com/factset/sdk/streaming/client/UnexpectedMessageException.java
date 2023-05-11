package com.factset.sdk.streaming.client;

public class UnexpectedMessageException extends ApiClientException {
    public UnexpectedMessageException(String message) {
        super(message);
    }
}
