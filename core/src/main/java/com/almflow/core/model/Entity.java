package com.almflow.core.model;

import java.time.Instant;
import java.util.Map;

/**
 * Normalized snapshot of an entity. Fields are a free-form map so connectors
 * can carry tool-specific values without polluting the SPI with subclasses.
 */
public record Entity(
        EntityRef ref,
        Map<String, Object> fields,
        Instant fetchedAt
) {
}
