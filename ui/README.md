# ALM Workflow Builder UI

Drag-and-drop workflow editor backed by the `app` REST API at `/api/workflows`.

## Run

```bash
npm install
npm run dev
```

Vite serves on http://localhost:5173 and proxies `/api/*` to the Spring Boot app at `localhost:8080`. Start the backend first (`mvn -f .. spring-boot:run -pl app`).

## What it does

- Lists saved workflows from the backend (left sidebar).
- Canvas renders one workflow as a linear graph: **Trigger → Condition → Action(s)**.
- Click a node to edit it in the right inspector.
- "+ Action" adds another action node wired off the condition.
- "Save" PUTs the workflow JSON to `/api/workflows/{id}`.

The on-disk JSON shape matches what the engine consumes — no translation layer. See [`../workflows/auto-escalate.json`](../workflows/auto-escalate.json).

## What's intentionally minimal

- v1 represents the condition tree as a single condition node. Nested `all` / `any` / `not` are editable as raw JSON in the inspector. A full tree editor is future work.
- No auth, no validation, no run-history view yet.
