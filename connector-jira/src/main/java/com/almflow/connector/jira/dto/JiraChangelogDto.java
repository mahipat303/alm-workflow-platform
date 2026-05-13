package com.almflow.connector.jira.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JiraChangelogDto(List<History> histories) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record History(String id, Author author, OffsetDateTime created, List<Item> items) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Author(String accountId, String displayName) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            String field,
            String fieldtype,
            @JsonProperty("fromString") String fromValue,
            @JsonProperty("toString") String toValue) {}
}
