import type { Node } from "reactflow";
import type { Condition } from "./types";
import type { ActionNodeData, ConditionNodeData, NodeData, TriggerNodeData } from "./lib/graph";

interface Props {
  node: Node<NodeData> | null;
  onChange: (data: NodeData) => void;
}

export function Inspector({ node, onChange }: Props) {
  if (!node) {
    return (
      <div className="inspector">
        <h3>Inspector</h3>
        <p style={{ fontSize: 13, color: "#666" }}>Select a node to edit.</p>
      </div>
    );
  }

  switch (node.data.kind) {
    case "trigger":
      return <TriggerEditor data={node.data} onChange={onChange} />;
    case "condition":
      return <ConditionEditor data={node.data} onChange={onChange} />;
    case "action":
      return <ActionEditor data={node.data} onChange={onChange} />;
  }
}

function TriggerEditor({ data, onChange }: { data: TriggerNodeData; onChange: (d: NodeData) => void }) {
  return (
    <div className="inspector">
      <h3>Trigger</h3>
      <label>Connector ID</label>
      <input
        value={data.trigger.connectorId}
        onChange={(e) => onChange({ ...data, trigger: { ...data.trigger, connectorId: e.target.value } })}
      />
      <label>Entity Type</label>
      <input
        value={data.trigger.entityType}
        onChange={(e) => onChange({ ...data, trigger: { ...data.trigger, entityType: e.target.value } })}
      />
    </div>
  );
}

function ConditionEditor({ data, onChange }: { data: ConditionNodeData; onChange: (d: NodeData) => void }) {
  const c = data.condition;
  const type = c?.type ?? "fieldTransition";

  const setType = (newType: Condition["type"]) => {
    let next: Condition;
    switch (newType) {
      case "fieldTransition":
        next = { type: "fieldTransition", field: "", from: null, to: null };
        break;
      case "fieldChanged":
        next = { type: "fieldChanged", field: "" };
        break;
      case "all":
        next = { type: "all", of: [] };
        break;
      case "any":
        next = { type: "any", of: [] };
        break;
      case "not":
        next = { type: "not", of: { type: "fieldChanged", field: "" } };
        break;
    }
    onChange({ ...data, condition: next });
  };

  return (
    <div className="inspector">
      <h3>Condition</h3>
      <label>Type</label>
      <select value={type} onChange={(e) => setType(e.target.value as Condition["type"])}>
        <option value="fieldTransition">field transition</option>
        <option value="fieldChanged">field changed</option>
        <option value="all">all of (raw JSON)</option>
        <option value="any">any of (raw JSON)</option>
        <option value="not">not (raw JSON)</option>
      </select>

      {c?.type === "fieldTransition" && (
        <>
          <label>Field</label>
          <input value={c.field} onChange={(e) => onChange({ ...data, condition: { ...c, field: e.target.value } })} />
          <label>From</label>
          <input
            value={c.from ?? ""}
            onChange={(e) => onChange({ ...data, condition: { ...c, from: e.target.value || null } })}
          />
          <label>To</label>
          <input
            value={c.to ?? ""}
            onChange={(e) => onChange({ ...data, condition: { ...c, to: e.target.value || null } })}
          />
        </>
      )}

      {c?.type === "fieldChanged" && (
        <>
          <label>Field</label>
          <input value={c.field} onChange={(e) => onChange({ ...data, condition: { ...c, field: e.target.value } })} />
        </>
      )}

      {c && (c.type === "all" || c.type === "any" || c.type === "not") && (
        <>
          <label>JSON</label>
          <textarea
            rows={10}
            value={JSON.stringify(c, null, 2)}
            onChange={(e) => {
              try {
                onChange({ ...data, condition: JSON.parse(e.target.value) });
              } catch {
                /* hold last valid value while user types */
              }
            }}
          />
        </>
      )}
    </div>
  );
}

function ActionEditor({ data, onChange }: { data: ActionNodeData; onChange: (d: NodeData) => void }) {
  const a = data.action;
  return (
    <div className="inspector">
      <h3>Action</h3>
      <label>Type</label>
      <input value={a.type} onChange={(e) => onChange({ ...data, action: { ...a, type: e.target.value } })} />
      <label>Target</label>
      <input value={a.target} onChange={(e) => onChange({ ...data, action: { ...a, target: e.target.value } })} />
      <label>Parameters (JSON)</label>
      <textarea
        rows={8}
        value={JSON.stringify(a.parameters ?? {}, null, 2)}
        onChange={(e) => {
          try {
            onChange({ ...data, action: { ...a, parameters: JSON.parse(e.target.value) } });
          } catch {
            /* ignore parse errors mid-edit */
          }
        }}
      />
    </div>
  );
}
