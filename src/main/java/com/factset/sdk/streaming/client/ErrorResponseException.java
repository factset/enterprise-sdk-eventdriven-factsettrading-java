package com.factset.sdk.streaming.client;

import java.util.List;

public class ErrorResponseException extends ApiClientException {

    @lombok.Getter
    private final List<Error> errorList;

    public ErrorResponseException(List<Error> errorList) {
        super("Received an ErrorResponse");
        this.errorList = errorList;
    }
}
