package com.almflow.core.event;

/**
 * Abstraction over the event bus. Connectors depend only on this interface;
 * the {@code app} module wires in a Kafka-backed implementation.
 */
public interface EventPublisher {
    void publish(EntityChangedEvent event);
}
