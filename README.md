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
- `engine` — workflow model, condition evaluator, action dispatcher, idempotency store.
- `app` — Spring Boot runtime that wires connectors, Kafka, the rule engine, and the workflow REST API.
- `ui` — React + React Flow drag-and-drop workflow builder ([`ui/README.md`](ui/README.md)).

## Build

```bash
mvn -q clean package
```

## Run (local)

```bash
docker compose up -d           # Kafka + Zookeeper
cd app && mvn spring-boot:run
```

## Workflow JSON

A workflow ties a trigger (connector + entity type) to a condition tree and one or more actions. The example below — *"when priority transitions Low → High, assign the issue to a specific user"* — lives at [`workflows/auto-escalate.json`](workflows/auto-escalate.json):

```json
{
  "id": "auto-escalate",
  "name": "Auto-assign on priority escalation",
  "enabled": true,
  "trigger": { "connectorId": "jira-default", "entityType": "issue" },
  "condition": {
    "type": "all",
    "of": [
      { "type": "fieldTransition", "field": "priority", "from": "Low", "to": "High" }
    ]
  },
  "actions": [
    { "type": "assign", "target": "$entity", "parameters": { "accountId": "..." } }
  ]
}
```

The drag-and-drop UI and the AI workflow-generator (Phase 3) both produce this same JSON shape — there is exactly one workflow format.

Drop additional `.json` files in `./workflows/` (or override `almflow.workflows.dir`) to register more.

## Roadmap

- **Phase 1 ✓**: connector SDK + Jira connector (webhook + poller) publishing events to Kafka.
- **Phase 2 ✓**: rule engine + workflow JSON schema + action dispatch with idempotency.
- **Phase 3 ✓ (builder)**: React Flow drag-and-drop workflow builder UI + REST API for workflows.
- **Phase 3 (next)**: AI workflow generator — natural language → workflow JSON via Claude.
