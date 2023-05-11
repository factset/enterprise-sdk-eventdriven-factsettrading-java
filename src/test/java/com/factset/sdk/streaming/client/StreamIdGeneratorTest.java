package com.factset.sdk.streaming.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StreamIdGeneratorTest {

    @Test
    void getNextIdWithDefaultValues() {
        StreamIdGenerator streamIdGenerator = new StreamIdGenerator();
        Assertions.assertEquals(2, streamIdGenerator.getNextStreamId());
        Assertions.assertEquals(4, streamIdGenerator.getNextStreamId());
    }

    @Test
    void getNextIdWithCustomValues() {
        StreamIdGenerator streamIdGenerator = new StreamIdGenerator(2, 3);
        Assertions.assertEquals(5, streamIdGenerator.getNextStreamId());
        Assertions.assertEquals(8, streamIdGenerator.getNextStreamId());
    }

    @Test
    void getNextIdTestOverflow() {
        StreamIdGenerator streamIdGenerator = new StreamIdGenerator(2147483640, 5);
        Assertions.assertEquals(2147483645, streamIdGenerator.getNextStreamId());
        Assertions.assertEquals(2147483645, streamIdGenerator.getNextStreamId());
    }
}
