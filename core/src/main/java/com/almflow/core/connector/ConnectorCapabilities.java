package com.almflow.core.connector;

import java.util.Set;

/**
 * Describes which trigger paths a connector supports. The runtime uses this
 * to decide whether to schedule a poller, register a webhook receiver, or both.
 */
public record ConnectorCapabilities(
        boolean supportsWebhooks,
        boolean supportsPolling,
        Set<String> supportedEntityTypes,
        Set<String> supportedActions
) {
}
