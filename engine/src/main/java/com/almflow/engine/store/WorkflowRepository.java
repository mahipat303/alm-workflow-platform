package com.almflow.engine.store;

import com.almflow.engine.workflow.Workflow;

import java.util.List;

public interface WorkflowRepository {

    List<Workflow> all();

    java.util.Optional<Workflow> findById(String id);

    /**
     * Return workflows whose {@code Trigger} matches the given connector and
     * entity type. Used by the engine to cheaply narrow candidates before
     * running the condition tree.
     */
    List<Workflow> forTrigger(String connectorId, String entityType);

    void save(Workflow workflow);

    void delete(String id);
}
