package com.factset.sdk.eventdriven.client;

public interface Message {
    String getType();
    <T> T parseAs(Class<T> messageType);
}
