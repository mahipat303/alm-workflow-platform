import { Handle, Position } from "reactflow";
import type { NodeProps } from "reactflow";
import type { TriggerNodeData } from "../lib/graph";

export function TriggerNode({ data }: NodeProps<TriggerNodeData>) {
  return (
    <div className="node node-trigger">
      <div className="node-title">Trigger</div>
      <div className="node-body">
        {data.trigger.connectorId || "(no connector)"} · {data.trigger.entityType || "(any)"}
      </div>
      <Handle type="source" position={Position.Right} />
    </div>
  );
}
