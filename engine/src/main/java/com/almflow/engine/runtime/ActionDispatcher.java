package com.almflow.engine.runtime;

import com.almflow.core.action.ActionRequest;
import com.almflow.core.action.ActionResult;
import com.almflow.core.connector.Connector;
import com.almflow.core.connector.ConnectorRegistry;
import com.almflow.core.event.EntityChangedEvent;
import com.almflow.core.model.EntityRef;
import com.almflow.engine.action.ActionSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ActionDispatcher {

    private static final Logger log = LoggerFactory.getLogger(ActionDispatcher.class);

    private final ConnectorRegistry registry;

    public ActionDispatcher(ConnectorRegistry registry) {
        this.registry = registry;
    }

    public ActionResult dispatch(EntityChangedEvent event, String workflowId, ActionSpec spec) {
        EntityRef target = resolveTarget(spec.target(), event);
        Optional<Connector> connector = registry.get(target.connectorId());
        if (connector.isEmpty()) {
            log.warn("no connector for {} (workflow {})", target.connectorId(), workflowId);
            return ActionResult.failure("connector not registered: " + target.connectorId());
        }
        String idem = event.idempotencyKey() + "::" + workflowId + "::" + spec.type();
        ActionRequest req = new ActionRequest(target, spec.type(), spec.parameters(), idem);
        return connector.get().applyAction(req);
    }

    private EntityRef resolveTarget(String target, EntityChangedEvent event) {
        if (target == null || target.equals("$entity")) return event.entityRef();
        throw new IllegalArgumentException("unsupported target reference: " + target);
    }
}
