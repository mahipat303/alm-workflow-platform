package com.almflow.connector.jira;

import com.almflow.connector.jira.dto.JiraWebhookPayload;
import com.almflow.connector.jira.internal.JiraIssueMapper;
import com.almflow.core.event.EntityChangedEvent;
import com.almflow.core.event.EventPublisher;
import com.almflow.core.model.EntityRef;
import com.almflow.core.model.FieldChange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Receives Jira issue-event webhooks and converts them to canonical
 * {@link EntityChangedEvent}s. Idempotency key combines the entity ref and
 * the webhook timestamp so duplicate deliveries collapse downstream.
 */
@RestController
@RequestMapping("/connectors/jira/webhook")
public class JiraWebhookController {

    private final JiraConnectorProperties props;
    private final EventPublisher publisher;

    public JiraWebhookController(JiraConnectorProperties props, EventPublisher publisher) {
        this.props = props;
        this.publisher = publisher;
    }

    @PostMapping
    public ResponseEntity<Void> receive(@RequestBody JiraWebhookPayload payload) {
        if (payload.issue() == null) {
            return ResponseEntity.accepted().build();
        }
        EntityRef ref = JiraIssueMapper.toRef(props.id(), payload.issue());
        List<FieldChange> changes = JiraIssueMapper.toFieldChanges(payload.changelog(), null);
        Instant observedAt = payload.timestamp() != null
                ? Instant.ofEpochMilli(payload.timestamp())
                : Instant.now();
        String idempotencyKey = ref.connectorId() + ":" + ref.externalId() + ":" + observedAt.toEpochMilli();

        publisher.publish(new EntityChangedEvent(
                UUID.randomUUID(),
                idempotencyKey,
                EntityChangedEvent.Source.WEBHOOK,
                ref,
                changes,
                observedAt));
        return ResponseEntity.accepted().build();
    }
}
