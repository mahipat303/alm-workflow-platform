package com.almflow.connector.jira.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JiraSearchResponse(int startAt, int maxResults, int total, List<JiraIssueDto> issues) {
}
