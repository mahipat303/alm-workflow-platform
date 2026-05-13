import type { Workflow } from "../types";

const BASE = "/api/workflows";

export async function listWorkflows(): Promise<Workflow[]> {
  const r = await fetch(BASE);
  if (!r.ok) throw new Error(`list failed: ${r.status}`);
  return r.json();
}

export async function getWorkflow(id: string): Promise<Workflow> {
  const r = await fetch(`${BASE}/${id}`);
  if (!r.ok) throw new Error(`get ${id} failed: ${r.status}`);
  return r.json();
}

export async function saveWorkflow(wf: Workflow): Promise<Workflow> {
  const r = await fetch(`${BASE}/${wf.id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(wf),
  });
  if (!r.ok) throw new Error(`save failed: ${r.status}`);
  return r.json();
}

export async function deleteWorkflow(id: string): Promise<void> {
  const r = await fetch(`${BASE}/${id}`, { method: "DELETE" });
  if (!r.ok) throw new Error(`delete failed: ${r.status}`);
}
