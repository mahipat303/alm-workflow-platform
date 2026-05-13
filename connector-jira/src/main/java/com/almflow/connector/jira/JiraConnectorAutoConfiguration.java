package com.almflow.connector.jira;

import com.almflow.connector.jira.internal.JiraRestClient;
import com.almflow.core.event.EventPublisher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@AutoConfiguration
@EnableConfigurationProperties(JiraConnectorProperties.class)
@ConditionalOnProperty(prefix = "almflow.connector.jira", name = "base-url")
@EnableScheduling
public class JiraConnectorAutoConfiguration {

    @Bean
    public JiraRestClient jiraRestClient(JiraConnectorProperties props) {
        return new JiraRestClient(props);
    }

    @Bean
    public JiraConnector jiraConnector(JiraConnectorProperties props, JiraRestClient rest) {
        return new JiraConnector(props, rest);
    }

    @Bean
    @ConditionalOnProperty(prefix = "almflow.connector.jira", name = "polling-enabled", havingValue = "true")
    public JiraPoller jiraPoller(JiraConnector connector, EventPublisher publisher) {
        return new JiraPoller(connector, publisher);
    }

    @Bean
    @ConditionalOnProperty(prefix = "almflow.connector.jira", name = "webhooks-enabled", havingValue = "true")
    public JiraWebhookController jiraWebhookController(JiraConnectorProperties props, EventPublisher publisher) {
        return new JiraWebhookController(props, publisher);
    }
}
