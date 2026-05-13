package com.almflow.app.kafka;

import com.almflow.core.event.EntityChangedEvent;
import com.almflow.engine.runtime.WorkflowEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);

    private final WorkflowEngine engine;

    public EventConsumer(WorkflowEngine engine) {
        this.engine = engine;
    }

    @KafkaListener(
            topics = "${almflow.kafka.events-topic:almflow.events.entity-changed}",
            groupId = "${almflow.kafka.consumer-group:almflow-engine}")
    public void onEvent(EntityChangedEvent event) {
        try {
            engine.onEvent(event);
        } catch (Exception e) {
            log.error("engine failed on event {}", event.idempotencyKey(), e);
        }
    }
}
