import { Handle, Position } from "reactflow";
import type { NodeProps } from "reactflow";
import type { ConditionNodeData } from "../lib/graph";

function summarize(c: ConditionNodeData["condition"]): string {
  if (!c) return "(no condition)";
  switch (c.type) {
    case "fieldTransition":
      return `${c.field}: ${c.from ?? "*"} → ${c.to ?? "*"}`;
    case "fieldChanged":
      return `${c.field} changed`;
    case "all":
      return `all of (${c.of.length})`;
    case "any":
      return `any of (${c.of.length})`;
    case "not":
      return "not (...)";
  }
}

export function ConditionNode({ data }: NodeProps<ConditionNodeData>) {
  return (
    <div className="node node-condition">
      <Handle type="target" position={Position.Left} />
      <div className="node-title">Condition</div>
      <div className="node-body">{summarize(data.condition)}</div>
      <Handle type="source" position={Position.Right} />
    </div>
  );
}
