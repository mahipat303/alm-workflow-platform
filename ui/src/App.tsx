import { useCallback, useEffect, useMemo, useState } from "react";
import ReactFlow, {
  Background,
  Controls,
  MiniMap,
  addEdge,
  useEdgesState,
  useNodesState,
} from "reactflow";
import type { Connection, Edge, Node } from "reactflow";
import { TriggerNode } from "./nodes/TriggerNode";
import { ConditionNode } from "./nodes/ConditionNode";
import { ActionNode } from "./nodes/ActionNode";
import { Inspector } from "./Inspector";
import { graphToWorkflow, workflowToGraph } from "./lib/graph";
import type { ActionNodeData, NodeData } from "./lib/graph";
import { deleteWorkflow, listWorkflows, saveWorkflow } from "./lib/api";
import type { Workflow } from "./types";

const nodeTypes = { trigger: TriggerNode, condition: ConditionNode, action: ActionNode };

const blank: Workflow = {
  id: "new-workflow",
  name: "New workflow",
  enabled: true,
  trigger: { connectorId: "jira-default", entityType: "issue" },
  condition: { type: "fieldTransition", field: "priority", from: "Low", to: "High" },
  actions: [{ type: "assign", target: "$entity", parameters: { accountId: "" } }],
};

export default function App() {
  const initial = useMemo(() => workflowToGraph(blank), []);
  const [nodes, setNodes, onNodesChange] = useNodesState<NodeData>(initial.nodes);
  const [edges, setEdges, onEdgesChange] = useEdgesState(initial.edges);
  const [selected, setSelected] = useState<Node<NodeData> | null>(null);
  const [list, setList] = useState<Workflow[]>([]);
  const [id, setId] = useState(blank.id);
  const [name, setName] = useState(blank.name);
  const [enabled, setEnabled] = useState(blank.enabled);
  const [banner, setBanner] = useState<string | null>(null);

  const refresh = useCallback(() => {
    listWorkflows()
      .then(setList)
      .catch((e) => setBanner(`backend unavailable: ${e.message}`));
  }, []);
  useEffect(refresh, [refresh]);

  const onConnect = useCallback(
    (c: Connection) => setEdges((eds) => addEdge(c, eds)),
    [setEdges],
  );

  const updateSelected = (data: NodeData) => {
    if (!selected) return;
    setNodes((ns) => ns.map((n) => (n.id === selected.id ? { ...n, data } : n)));
    setSelected((s) => (s ? { ...s, data } : s));
  };

  const addAction = () => {
    const i = nodes.filter((n) => n.data.kind === "action").length;
    const newNode: Node<ActionNodeData> = {
      id: `action-${Date.now()}`,
      type: "action",
      position: { x: 620, y: 80 + i * 140 },
      data: { kind: "action", action: { type: "assign", target: "$entity", parameters: {} } },
    };
    setNodes((ns) => [...ns, newNode]);
    setEdges((es) => [...es, { id: `e-c-${newNode.id}`, source: "condition", target: newNode.id }]);
  };

  const load = async (wfId: string) => {
    const wf = list.find((w) => w.id === wfId);
    if (!wf) return;
    const g = workflowToGraph(wf);
    setNodes(g.nodes);
    setEdges(g.edges);
    setId(wf.id);
    setName(wf.name);
    setEnabled(wf.enabled);
    setSelected(null);
  };

  const save = async () => {
    const wf = graphToWorkflow(id, name, enabled, nodes);
    try {
      await saveWorkflow(wf);
      setBanner(`saved ${wf.id}`);
      refresh();
    } catch (e: unknown) {
      setBanner(`save failed: ${(e as Error).message}`);
    }
  };

  const remove = async () => {
    if (!confirm(`Delete ${id}?`)) return;
    await deleteWorkflow(id);
    refresh();
  };

  return (
    <div className="app">
      <div className="sidebar">
        <h3>Workflows</h3>
        <div className="workflow-list">
          {list.map((w) => (
            <button key={w.id} onClick={() => load(w.id)}>
              {w.name} <span style={{ color: "#999" }}>· {w.id}</span>
            </button>
          ))}
          {list.length === 0 && <div style={{ fontSize: 12, color: "#999" }}>None yet</div>}
        </div>
        <h3>Add node</h3>
        <button onClick={addAction}>+ Action</button>
      </div>

      <div className="canvas-wrap">
        <div className="toolbar">
          <input value={id} onChange={(e) => setId(e.target.value)} placeholder="id" style={{ width: 140 }} />
          <input value={name} onChange={(e) => setName(e.target.value)} placeholder="name" style={{ width: 220 }} />
          <label style={{ fontSize: 13, display: "flex", alignItems: "center", gap: 6 }}>
            <input type="checkbox" checked={enabled} onChange={(e) => setEnabled(e.target.checked)} /> enabled
          </label>
          <div className="spacer" />
          <button onClick={save}>Save</button>
          <button onClick={remove}>Delete</button>
        </div>
        {banner && <div className="banner">{banner}</div>}
        <div className="canvas">
          <ReactFlow
            nodes={nodes}
            edges={edges}
            onNodesChange={onNodesChange}
            onEdgesChange={onEdgesChange}
            onConnect={onConnect}
            onNodeClick={(_, n) => setSelected(n)}
            onPaneClick={() => setSelected(null)}
            nodeTypes={nodeTypes}
            fitView
          >
            <Background />
            <Controls />
            <MiniMap />
          </ReactFlow>
        </div>
      </div>

      <Inspector node={selected} onChange={updateSelected} />
    </div>
  );
}
