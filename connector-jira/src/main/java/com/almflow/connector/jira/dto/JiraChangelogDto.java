package com.almflow.connector.jira.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JiraChangelogDto(List<History> histories) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record History(String id, Author author, OffsetDateTime created, List<Item> items) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Author(String accountId, String displayName) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(String field, String fieldtype, String fromString, String toString) {}
}
