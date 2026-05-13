package com.almflow.engine.store;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks which (event idempotency key, workflow id) pairs have already been
 * acted on, so the engine doesn't double-fire when the same change arrives
 * via both webhook and poll. Process-local for now; a Redis-backed impl is
 * the natural production replacement.
 */
public class IdempotencyStore {

    private final Duration ttl;
    private final Map<String, Instant> seen = new ConcurrentHashMap<>();

    public IdempotencyStore(Duration ttl) {
        this.ttl = ttl;
    }

    public boolean tryClaim(String eventIdempotencyKey, String workflowId) {
        String key = eventIdempotencyKey + "::" + workflowId;
        purgeIfNeeded();
        return seen.putIfAbsent(key, Instant.now()) == null;
    }

    private void purgeIfNeeded() {
        if (seen.size() < 10_000) return;
        Instant cutoff = Instant.now().minus(ttl);
        seen.entrySet().removeIf(e -> e.getValue().isBefore(cutoff));
    }
}
