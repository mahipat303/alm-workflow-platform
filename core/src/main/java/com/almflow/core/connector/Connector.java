package com.almflow.core.connector;

import com.almflow.core.action.ActionRequest;
import com.almflow.core.action.ActionResult;
import com.almflow.core.model.Entity;
import com.almflow.core.model.EntityRef;
import com.almflow.core.model.FieldChange;

import java.time.Instant;
import java.util.List;

/**
 * SPI every ALM connector implements. The platform interacts with connectors
 * only through this interface — Jira-specific types must not leak out.
 */
public interface Connector {

    /** Stable identifier for this connector instance, e.g. "jira-prod". */
    String id();

    /** Connector capability descriptor — drives whether the platform schedules polling for it. */
    ConnectorCapabilities capabilities();

    /** Fetch a single entity by reference. */
    Entity fetchEntity(EntityRef ref);

    /**
     * Fetch field-level history for an entity since {@code since}.
     * Required so we can detect transitions like "priority low → high"
     * regardless of whether the trigger was a webhook or a poll.
     */
    List<FieldChange> fetchHistory(EntityRef ref, Instant since);

    /**
     * List entities modified since {@code since}, used by the poller.
     * Connectors that only support webhooks may return an empty list.
     */
    List<EntityRef> listChangedSince(Instant since);

    /** Execute an action (assign, transition, comment, ...) on a target entity. */
    ActionResult applyAction(ActionRequest request);
}
