package com.almflow.app.config;

import com.almflow.core.connector.ConnectorRegistry;
import com.almflow.engine.runtime.ActionDispatcher;
import com.almflow.engine.runtime.WorkflowEngine;
import com.almflow.engine.store.FileWorkflowRepository;
import com.almflow.engine.store.IdempotencyStore;
import com.almflow.engine.store.WorkflowRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.time.Duration;

@Configuration
public class EngineConfig {

    @Bean
    public WorkflowRepository workflowRepository(
            @Value("${almflow.workflows.dir:./workflows}") String dir,
            ObjectMapper objectMapper) {
        return new FileWorkflowRepository(Path.of(dir), objectMapper);
    }

    @Bean
    public IdempotencyStore idempotencyStore() {
        return new IdempotencyStore(Duration.ofHours(6));
    }

    @Bean
    public ActionDispatcher actionDispatcher(ConnectorRegistry registry) {
        return new ActionDispatcher(registry);
    }

    @Bean
    public WorkflowEngine workflowEngine(WorkflowRepository repo,
                                         ActionDispatcher dispatcher,
                                         IdempotencyStore idempotency) {
        return new WorkflowEngine(repo, dispatcher, idempotency);
    }
}
