package com.almflow.app.config;

import com.almflow.core.connector.Connector;
import com.almflow.core.connector.ConnectorRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Builds a {@link ConnectorRegistry} from every {@link Connector} bean
 * Spring discovers (including connectors contributed by auto-configurations
 * like {@code connector-jira}). New connector modules just register a Connector
 * bean — no changes needed here.
 */
@Configuration
public class ConnectorRegistryConfig {

    @Bean
    public ConnectorRegistry connectorRegistry(List<Connector> connectors) {
        Map<String, Connector> byId = connectors.stream()
                .collect(Collectors.toMap(Connector::id, Function.identity()));
        return id -> Optional.ofNullable(byId.get(id));
    }
}
