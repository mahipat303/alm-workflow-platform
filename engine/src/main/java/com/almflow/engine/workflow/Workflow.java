package com.almflow.engine.workflow;

import com.almflow.engine.action.ActionSpec;
import com.almflow.engine.condition.Condition;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Top-level workflow definition. Serialized as JSON; the same shape is produced
 * by the (future) drag-and-drop UI and by the AI workflow-generator.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Workflow(
        String id,
        String name,
        boolean enabled,
        Trigger trigger,
        Condition condition,
        List<ActionSpec> actions
) {
    /**
     * Coarse trigger filter — applied before the condition tree to cheaply
     * skip events from the wrong connector or entity type.
     */
    public record Trigger(String connectorId, String entityType) {}
}
