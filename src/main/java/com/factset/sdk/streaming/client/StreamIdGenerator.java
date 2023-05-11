package com.factset.sdk.streaming.client;

import java.util.concurrent.atomic.AtomicInteger;

class StreamIdGenerator {

    private final AtomicInteger streamId;

    private final int incrementStep;
    private final int startValue;

    public StreamIdGenerator() {
        this(0, 2);
    }

    public StreamIdGenerator(int startValue, int incrementStep) {
        this.incrementStep = incrementStep;
        this.startValue = startValue;
        streamId = new AtomicInteger(startValue);
    }

    public int getNextStreamId() {
        return streamId.updateAndGet(currentId -> {

            int nextId = currentId + incrementStep;

            if (nextId < 0) {
                nextId = startValue + incrementStep;
            }

            return nextId;
        });
    }
}
