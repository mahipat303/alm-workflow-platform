package com.almflow.app.kafka;

import com.almflow.core.event.EntityChangedEvent;
import com.almflow.core.event.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaTemplate<String, EntityChangedEvent> template;
    private final String topic;

    public KafkaEventPublisher(KafkaTemplate<String, EntityChangedEvent> template, String topic) {
        this.template = template;
        this.topic = topic;
    }

    @Override
    public void publish(EntityChangedEvent event) {
        String partitionKey = event.entityRef().connectorId() + ":" + event.entityRef().externalId();
        template.send(topic, partitionKey, event);
        log.debug("published {} ({} changes) to {}", event.idempotencyKey(), event.changes().size(), topic);
    }
}
