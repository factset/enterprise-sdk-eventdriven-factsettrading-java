package com.factset.sdk.streaming.client;

public interface WebsocketCloseCode {
    int NORMAL_CLOSURE = 1000;
    int PROTOCOL_ERROR = 1002;
    int POLICY_VIOLATION = 1008;
}
