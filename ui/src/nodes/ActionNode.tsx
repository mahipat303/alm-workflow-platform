import { Handle, Position } from "reactflow";
import type { NodeProps } from "reactflow";
import type { ActionNodeData } from "../lib/graph";

export function ActionNode({ data }: NodeProps<ActionNodeData>) {
  const a = data.action;
  return (
    <div className="node node-action">
      <Handle type="target" position={Position.Left} />
      <div className="node-title">Action · {a.type}</div>
      <div className="node-body">
        target: {a.target}
        {a.parameters && Object.keys(a.parameters).length > 0 && (
          <div style={{ marginTop: 4, fontSize: 11, color: "#555" }}>
            {Object.entries(a.parameters).map(([k, v]) => (
              <div key={k}>
                {k}: {String(v)}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
