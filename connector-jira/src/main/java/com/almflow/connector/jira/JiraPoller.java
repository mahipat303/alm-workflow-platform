package com.almflow.connector.jira;

import com.almflow.core.event.EntityChangedEvent;
import com.almflow.core.event.EventPublisher;
import com.almflow.core.model.EntityRef;
import com.almflow.core.model.FieldChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Fallback / reconciliation poller. Runs even when webhooks are configured,
 * so that webhook drops, network gaps, or webhook-less tenants still produce
 * events. The rule engine relies on idempotency keys to drop duplicates that
 * appear via both paths.
 */
public class JiraPoller {

    private static final Logger log = LoggerFactory.getLogger(JiraPoller.class);

    private final JiraConnector connector;
    private final EventPublisher publisher;
    private volatile Instant lastRun = Instant.now().minusSeconds(300);

    public JiraPoller(JiraConnector connector, EventPublisher publisher) {
        this.connector = connector;
        this.publisher = publisher;
    }

    @Scheduled(fixedDelayString = "${almflow.connector.jira.poll-interval-ms:60000}")
    public void poll() {
        Instant since = lastRun;
        Instant now = Instant.now();
        try {
            List<EntityRef> changed = connector.listChangedSince(since);
            for (EntityRef ref : changed) {
                List<FieldChange> history = connector.fetchHistory(ref, since);
                if (history.isEmpty()) continue;
                String idempotencyKey = ref.connectorId() + ":" + ref.externalId() + ":poll:" + now.toEpochMilli();
                publisher.publish(new EntityChangedEvent(
                        UUID.randomUUID(),
                        idempotencyKey,
                        EntityChangedEvent.Source.POLL,
                        ref,
                        history,
                        now));
            }
            lastRun = now;
        } catch (Exception e) {
            log.warn("Jira poll failed; will retry next tick", e);
        }
    }
}
