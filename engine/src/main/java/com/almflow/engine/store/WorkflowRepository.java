package com.almflow.engine.store;

import com.almflow.engine.workflow.Workflow;

import java.util.List;

public interface WorkflowRepository {

    List<Workflow> all();

    /**
     * Return workflows whose {@code Trigger} matches the given connector and
     * entity type. Used by the engine to cheaply narrow candidates before
     * running the condition tree.
     */
    List<Workflow> forTrigger(String connectorId, String entityType);
}
