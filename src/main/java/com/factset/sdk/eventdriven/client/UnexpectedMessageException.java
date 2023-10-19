package com.factset.sdk.eventdriven.client;

import lombok.Getter;

@Getter
public class UnexpectedMessageException extends ApiClientException {
    private final Message unexpectedMessage;
    private final String expectedType;

    public UnexpectedMessageException(String expectedType, Message unexpectedMessage) {
        super(String.format(
                "Unexpected message received. Expected: %s. Received: %s",
                expectedType,
                unexpectedMessage.getType()
        ));
        this.expectedType = expectedType;
        this.unexpectedMessage = unexpectedMessage;
    }

}
