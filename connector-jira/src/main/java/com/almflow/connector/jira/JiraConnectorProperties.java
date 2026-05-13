package com.almflow.connector.jira;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "almflow.connector.jira")
public record JiraConnectorProperties(
        String id,
        String baseUrl,
        String email,
        String apiToken,
        String pollJql,
        boolean pollingEnabled,
        boolean webhooksEnabled
) {
}
