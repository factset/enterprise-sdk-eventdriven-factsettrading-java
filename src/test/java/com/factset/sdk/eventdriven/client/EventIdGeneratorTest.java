package com.factset.sdk.eventdriven.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EventIdGeneratorTest {

    @Test
    void getNextIdWithDefaultValues() {
        EventIdGenerator eventIdGenerator = new EventIdGenerator();
        Assertions.assertEquals(2, eventIdGenerator.getNextEventId());
        Assertions.assertEquals(4, eventIdGenerator.getNextEventId());
    }

    @Test
    void getNextIdWithCustomValues() {
        EventIdGenerator eventIdGenerator = new EventIdGenerator(2, 3);
        Assertions.assertEquals(5, eventIdGenerator.getNextEventId());
        Assertions.assertEquals(8, eventIdGenerator.getNextEventId());
    }

    @Test
    void getNextIdTestOverflow() {
        EventIdGenerator eventIdGenerator = new EventIdGenerator(2147483640, 5);
        Assertions.assertEquals(2147483645, eventIdGenerator.getNextEventId());
        Assertions.assertEquals(2147483645, eventIdGenerator.getNextEventId());
    }
}
