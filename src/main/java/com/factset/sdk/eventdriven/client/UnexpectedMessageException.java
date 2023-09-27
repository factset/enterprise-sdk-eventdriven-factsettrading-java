package com.factset.sdk.eventdriven.client;

import lombok.Getter;

@Getter
public class UnexpectedMessageException extends ApiClientException {
    private final Message unexpectedMessage;

    public UnexpectedMessageException(String message, Message unexpectedMessage) {
        super(message);
        this.unexpectedMessage = unexpectedMessage;
    }

}
