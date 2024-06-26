package com.example.demo.util;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

public class PerformanceUtil {
    private static HashMap<String, Instant> instantHashMap = new HashMap<>();

    /**
     * Initialize performance test
     * @param alias
     * @return Instant initiation.
     */
    public static Instant create(String alias) {
        final var now = Instant.now();
        instantHashMap.put(alias, now);
        return now;
    }

    /**
     * Ends the performance test clearing the aliases
     * @param alias
     * @return The time diff between start and end.
     */
    public static Duration end(String alias) {
        if (instantHashMap.containsKey(alias)) {
            final Instant end = Instant.now();
            final Instant start = instantHashMap.remove(alias);
            return Duration.between(start, end);
        }

        throw new IllegalArgumentException("Invalid 'alias'!");
    }

    /**
     * Test performance test.
     * @param alias
     * @return The time diff between start and end.
     */
    public static Duration compare(String alias) {
        if (instantHashMap.containsKey(alias)) {
            final Instant end = Instant.now();
            final Instant start = instantHashMap.get(alias);
            return Duration.between(start, end);
        }

        throw new IllegalArgumentException("Invalid 'alias'!");
    }

}
