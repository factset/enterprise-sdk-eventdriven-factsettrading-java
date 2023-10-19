package com.factset.sdk.eventdriven.client;

public interface Message {
    /**
     * Returns the type of the message.
     */
    String getType();

    /**
     * Parses this message as the given class.
     * @param messageType type to parse the message as
     * @return the parsed message
     * @throws UnexpectedMessageException when this message's type does not match the given messageType class.
     * @throws MalformedMessageException when the message can not be parsed properly as the given class.
     */
    <T> T parseAs(Class<T> messageType) throws UnexpectedMessageException, MalformedMessageException;
}
