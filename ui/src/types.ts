// Mirrors the backend Workflow JSON schema produced by the engine module.
// Keeping this small and centralized: the canvas, the API client, and any
// future AI-generator all serialize to this exact shape.

export type Condition =
  | { type: "all"; of: Condition[] }
  | { type: "any"; of: Condition[] }
  | { type: "not"; of: Condition }
  | { type: "fieldTransition"; field: string; from?: string | null; to?: string | null }
  | { type: "fieldChanged"; field: string };

export interface ActionSpec {
  type: string;
  target: string;
  parameters: Record<string, unknown>;
}

export interface Trigger {
  connectorId: string;
  entityType: string;
}

export interface Workflow {
  id: string;
  name: string;
  enabled: boolean;
  trigger: Trigger;
  condition: Condition | null;
  actions: ActionSpec[];
}
