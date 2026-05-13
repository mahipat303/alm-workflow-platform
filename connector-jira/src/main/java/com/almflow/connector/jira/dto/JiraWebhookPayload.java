package com.almflow.connector.jira.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

/**
 * Issue-event webhook from Jira. {@code changelog} carries the field deltas
 * that triggered the event (priority, assignee, status, ...).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record JiraWebhookPayload(
        Long timestamp,
        String webhookEvent,
        String issue_event_type_name,
        JiraIssueDto issue,
        JiraChangelogDto changelog,
        OffsetDateTime updated
) {
}
