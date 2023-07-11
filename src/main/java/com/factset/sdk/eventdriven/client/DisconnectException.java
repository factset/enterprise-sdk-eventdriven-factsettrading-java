package com.factset.sdk.eventdriven.client;

import lombok.Getter;

public class DisconnectException extends ApiClientException {

    @Getter
    private final int code;

    @Getter
    private final String reason;

    public DisconnectException(int code, String reason, Throwable t) {
        super(t);
        this.code = code;
        this.reason = reason;
    }

    public DisconnectException(int code, String reason) {
        super("Disconnect called");
        this.code = code;
        this.reason = reason;
    }
}
