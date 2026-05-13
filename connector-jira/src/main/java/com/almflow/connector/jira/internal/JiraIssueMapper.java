package com.almflow.connector.jira.internal;

import com.almflow.connector.jira.dto.JiraChangelogDto;
import com.almflow.connector.jira.dto.JiraIssueDto;
import com.almflow.core.model.Entity;
import com.almflow.core.model.EntityRef;
import com.almflow.core.model.FieldChange;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Translates Jira-shaped payloads into platform-neutral model types.
 * Keeping mapping concentrated here is what stops Jira specifics from
 * leaking into {@code core}.
 */
public final class JiraIssueMapper {

    private JiraIssueMapper() {}

    public static EntityRef toRef(String connectorId, JiraIssueDto issue) {
        return new EntityRef(connectorId, "issue", issue.key());
    }

    public static Entity toEntity(String connectorId, JiraIssueDto issue) {
        Map<String, Object> fields = issue.fields() == null ? Map.of() : Map.copyOf(issue.fields());
        return new Entity(toRef(connectorId, issue), fields, Instant.now());
    }

    public static List<FieldChange> toFieldChanges(JiraChangelogDto changelog, Instant since) {
        if (changelog == null || changelog.histories() == null) return List.of();
        return changelog.histories().stream()
                .filter(h -> since == null || h.created() == null
                        || h.created().toInstant().isAfter(since))
                .flatMap(h -> h.items().stream().map(item -> new FieldChange(
                        item.field(),
                        item.fromValue(),
                        item.toValue(),
                        h.author() == null ? null : h.author().accountId(),
                        h.created() == null ? Instant.now() : h.created().toInstant())))
                .filter(Objects::nonNull)
                .toList();
    }
}
