package com.almflow.core.event;

import com.almflow.core.model.EntityRef;
import com.almflow.core.model.FieldChange;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Canonical event published to Kafka whenever a connector observes a change
 * in an external entity. Webhook and polling paths both produce this shape.
 *
 * eventId is unique per emission; idempotencyKey is derived from
 * (entityRef + observedAt + field deltas) so duplicate emissions from
 * webhook+polling overlap can be de-duped downstream.
 */
public record EntityChangedEvent(
        UUID eventId,
        String idempotencyKey,
        Source source,
        EntityRef entityRef,
        List<FieldChange> changes,
        Instant observedAt
) {
    public enum Source { WEBHOOK, POLL }
}
