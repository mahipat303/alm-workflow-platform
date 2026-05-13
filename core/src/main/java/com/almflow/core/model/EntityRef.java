package com.almflow.core.model;

/**
 * Stable identity of an entity in a downstream ALM tool.
 * connectorId = which connector instance owns it (e.g. "jira-prod").
 * type        = entity type within that tool (e.g. "bug", "story", "issue").
 * externalId  = the tool's native id (e.g. Jira issue key "PROJ-123").
 */
public record EntityRef(String connectorId, String type, String externalId) {
}
