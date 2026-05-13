package com.almflow.connector.jira.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * Minimal projection of a Jira issue. Untyped {@code fields} map keeps the
 * connector resilient to custom-field configurations across tenants.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record JiraIssueDto(
        String id,
        String key,
        Map<String, Object> fields,
        JiraChangelogDto changelog
) {
}
