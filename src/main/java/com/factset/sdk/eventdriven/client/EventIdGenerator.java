package com.factset.sdk.eventdriven.client;

import java.util.concurrent.atomic.AtomicInteger;

class EventIdGenerator {

    private final AtomicInteger eventId;

    private final int incrementStep;
    private final int startValue;

    public EventIdGenerator() {
        this(0, 2);
    }

    public EventIdGenerator(int startValue, int incrementStep) {
        this.incrementStep = incrementStep;
        this.startValue = startValue;
        eventId = new AtomicInteger(startValue);
    }

    public int getNextEventId() {
        return eventId.updateAndGet(currentId -> {

            int nextId = currentId + incrementStep;

            if (nextId < 0) {
                nextId = startValue + incrementStep;
            }

            return nextId;
        });
    }
}
