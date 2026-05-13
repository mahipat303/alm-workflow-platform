package com.almflow.core.action;

import com.almflow.core.model.EntityRef;

import java.util.Map;

/**
 * Connector-agnostic action descriptor. The {@code type} is a logical action
 * name (e.g. "assign", "transition", "comment"); {@code parameters} carry the
 * inputs. Each connector documents which action types it understands.
 */
public record ActionRequest(
        EntityRef target,
        String type,
        Map<String, Object> parameters,
        String idempotencyKey
) {
}
