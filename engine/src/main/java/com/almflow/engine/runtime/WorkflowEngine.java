package com.almflow.engine.runtime;

import com.almflow.core.action.ActionResult;
import com.almflow.core.event.EntityChangedEvent;
import com.almflow.engine.action.ActionSpec;
import com.almflow.engine.condition.ConditionEvaluator;
import com.almflow.engine.store.IdempotencyStore;
import com.almflow.engine.store.WorkflowRepository;
import com.almflow.engine.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Core matcher. For each incoming event:
 *   1. ask the repository for workflows whose Trigger matches (cheap filter)
 *   2. evaluate the condition tree
 *   3. claim idempotency for (event, workflow) — drops duplicates that arrive
 *      via webhook+poll
 *   4. dispatch each action
 */
public class WorkflowEngine {

    private static final Logger log = LoggerFactory.getLogger(WorkflowEngine.class);

    private final WorkflowRepository repository;
    private final ActionDispatcher dispatcher;
    private final IdempotencyStore idempotency;

    public WorkflowEngine(WorkflowRepository repository,
                          ActionDispatcher dispatcher,
                          IdempotencyStore idempotency) {
        this.repository = repository;
        this.dispatcher = dispatcher;
        this.idempotency = idempotency;
    }

    public void onEvent(EntityChangedEvent event) {
        List<Workflow> candidates = repository.forTrigger(
                event.entityRef().connectorId(),
                event.entityRef().type());

        for (Workflow workflow : candidates) {
            if (workflow.condition() != null
                    && !ConditionEvaluator.evaluate(workflow.condition(), event)) {
                continue;
            }
            if (!idempotency.tryClaim(event.idempotencyKey(), workflow.id())) {
                log.debug("dedup: workflow {} already fired for {}", workflow.id(), event.idempotencyKey());
                continue;
            }
            fire(event, workflow);
        }
    }

    private void fire(EntityChangedEvent event, Workflow workflow) {
        log.info("firing workflow {} for {}", workflow.id(), event.entityRef());
        for (ActionSpec spec : workflow.actions()) {
            try {
                ActionResult result = dispatcher.dispatch(event, workflow.id(), spec);
                if (!result.success()) {
                    log.warn("workflow {} action {} failed: {}", workflow.id(), spec.type(), result.message());
                }
            } catch (Exception e) {
                log.error("workflow {} action {} threw", workflow.id(), spec.type(), e);
            }
        }
    }
}
