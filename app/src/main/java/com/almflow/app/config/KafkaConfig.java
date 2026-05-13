package com.almflow.app.config;

import com.almflow.app.kafka.KafkaEventPublisher;
import com.almflow.core.event.EntityChangedEvent;
import com.almflow.core.event.EventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Bean
    public KafkaTemplate<String, EntityChangedEvent> kafkaTemplate(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String bootstrap,
            ObjectMapper objectMapper) {
        Map<String, Object> cfg = new HashMap<>();
        cfg.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        cfg.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        cfg.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        cfg.put(ProducerConfig.ACKS_CONFIG, "all");
        cfg.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        JsonSerializer<EntityChangedEvent> valueSer = new JsonSerializer<>(objectMapper);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(cfg, new StringSerializer(), valueSer));
    }

    @Bean
    public EventPublisher eventPublisher(
            KafkaTemplate<String, EntityChangedEvent> template,
            @Value("${almflow.kafka.events-topic:almflow.events.entity-changed}") String topic) {
        return new KafkaEventPublisher(template, topic);
    }
}
