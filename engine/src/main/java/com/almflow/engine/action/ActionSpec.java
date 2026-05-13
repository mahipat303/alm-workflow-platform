package com.almflow.engine.action;

import java.util.Map;

/**
 * Workflow-side description of an action. The engine materializes this into
 * an {@code ActionRequest} at fire time, resolving {@code target} (e.g.
 * "$entity" → the event's entity) and substituting parameters.
 */
public record ActionSpec(
        String type,
        String target,
        Map<String, Object> parameters
) {
}
