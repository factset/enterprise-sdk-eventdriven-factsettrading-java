package com.factset.sdk.streaming.client;

public class ApiClientException extends RuntimeException {
    public ApiClientException(String message) {
        super(message);
    }

    public ApiClientException(Throwable cause) {
        super(cause);
    }
}
