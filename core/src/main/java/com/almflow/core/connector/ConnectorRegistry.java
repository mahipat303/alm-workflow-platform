package com.almflow.core.connector;

import java.util.Optional;

/**
 * Runtime lookup from connectorId (as carried in EntityRef) to a Connector bean.
 * The engine depends on this rather than wiring a specific connector type, so
 * it works uniformly across Jira, ADO, Rally, ...
 */
public interface ConnectorRegistry {
    Optional<Connector> get(String connectorId);
}
