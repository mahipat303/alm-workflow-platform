# ALM Workflow Platform

Custom workflow automation engine for ALM / bug-tracking tools (Jira, Azure DevOps, Rally, ...).

Users define rules like *"when bug priority transitions Low → High, assign to senior on-call engineer"* and the platform executes them across configured ALM tools.

## Architecture

```
            +---------------------+
            |  Webhook receivers  |---+
            |  (per connector)    |   |
            +---------------------+   |    +----------------+
                                      +--> |     Kafka      | --> Rule Engine --> Action Executor
            +---------------------+   |    |  (events bus)  |                          |
            |  Scheduler / Poller |---+    +----------------+                          v
            |  (fallback)         |                                              Connector.applyAction
            +---------------------+
```

Triggers are **hybrid**: webhooks where the tool supports them, scheduled polling as fallback. Both normalize to the same `EntityChangedEvent` published to Kafka.

## Modules

- `core` — connector SPI, event schema, domain model (no Spring beans, pure Java).
- `connector-jira` — Jira REST client, webhook controller, scheduled poller. Reference connector implementation.
- `app` — Spring Boot runtime that wires connectors, Kafka, and (later) the rule engine.

## Build

```bash
mvn -q clean package
```

## Run (local)

```bash
docker compose up -d           # Kafka + Zookeeper
cd app && mvn spring-boot:run
```

## Roadmap

- **Phase 1 (current)**: connector SDK + Jira connector (webhook + poller) publishing events to Kafka.
- **Phase 2**: rule engine + workflow JSON schema + action dispatch.
- **Phase 3**: React Flow drag-and-drop workflow builder UI + AI workflow generation.
