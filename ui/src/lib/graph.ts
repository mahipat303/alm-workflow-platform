import type { Edge, Node } from "reactflow";
import type { ActionSpec, Condition, Trigger, Workflow } from "../types";

// v1 graph shape is intentionally linear: trigger -> condition -> action(s).
// The condition node holds the entire condition tree as JSON so users can
// nest All/Any/Not without us building a full tree editor on day one.

export interface TriggerNodeData { kind: "trigger"; trigger: Trigger }
export interface ConditionNodeData { kind: "condition"; condition: Condition | null }
export interface ActionNodeData { kind: "action"; action: ActionSpec }
export type NodeData = TriggerNodeData | ConditionNodeData | ActionNodeData;

export function workflowToGraph(wf: Workflow): { nodes: Node<NodeData>[]; edges: Edge[] } {
  const nodes: Node<NodeData>[] = [];
  const edges: Edge[] = [];

  nodes.push({
    id: "trigger",
    type: "trigger",
    position: { x: 80, y: 200 },
    data: { kind: "trigger", trigger: wf.trigger },
  });

  nodes.push({
    id: "condition",
    type: "condition",
    position: { x: 340, y: 200 },
    data: { kind: "condition", condition: wf.condition },
  });
  edges.push({ id: "e-t-c", source: "trigger", target: "condition" });

  wf.actions.forEach((a, i) => {
    const id = `action-${i}`;
    nodes.push({
      id,
      type: "action",
      position: { x: 620, y: 80 + i * 140 },
      data: { kind: "action", action: a },
    });
    edges.push({ id: `e-c-${id}`, source: "condition", target: id });
  });

  return { nodes, edges };
}

export function graphToWorkflow(
  id: string,
  name: string,
  enabled: boolean,
  nodes: Node<NodeData>[],
): Workflow {
  const trigger = nodes.find((n) => n.data.kind === "trigger")?.data as TriggerNodeData | undefined;
  const condition = nodes.find((n) => n.data.kind === "condition")?.data as ConditionNodeData | undefined;
  const actions = nodes
    .filter((n) => n.data.kind === "action")
    .map((n) => (n.data as ActionNodeData).action);

  return {
    id,
    name,
    enabled,
    trigger: trigger?.trigger ?? { connectorId: "", entityType: "" },
    condition: condition?.condition ?? null,
    actions,
  };
}
