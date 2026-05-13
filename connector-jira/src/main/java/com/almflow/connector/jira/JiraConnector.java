package com.almflow.connector.jira;

import com.almflow.connector.jira.dto.JiraIssueDto;
import com.almflow.connector.jira.dto.JiraSearchResponse;
import com.almflow.connector.jira.internal.JiraIssueMapper;
import com.almflow.connector.jira.internal.JiraRestClient;
import com.almflow.core.action.ActionRequest;
import com.almflow.core.action.ActionResult;
import com.almflow.core.connector.Connector;
import com.almflow.core.connector.ConnectorCapabilities;
import com.almflow.core.model.Entity;
import com.almflow.core.model.EntityRef;
import com.almflow.core.model.FieldChange;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public class JiraConnector implements Connector {

    private final JiraConnectorProperties props;
    private final JiraRestClient rest;

    public JiraConnector(JiraConnectorProperties props, JiraRestClient rest) {
        this.props = props;
        this.rest = rest;
    }

    @Override
    public String id() {
        return props.id();
    }

    @Override
    public ConnectorCapabilities capabilities() {
        return new ConnectorCapabilities(
                props.webhooksEnabled(),
                props.pollingEnabled(),
                Set.of("issue"),
                Set.of("assign", "transition", "comment"));
    }

    @Override
    public Entity fetchEntity(EntityRef ref) {
        JiraIssueDto issue = rest.getIssue(ref.externalId(), false);
        return JiraIssueMapper.toEntity(props.id(), issue);
    }

    @Override
    public List<FieldChange> fetchHistory(EntityRef ref, Instant since) {
        JiraIssueDto issue = rest.getIssue(ref.externalId(), true);
        return JiraIssueMapper.toFieldChanges(issue.changelog(), since);
    }

    @Override
    public List<EntityRef> listChangedSince(Instant since) {
        String jql = props.pollJql() != null ? props.pollJql() : "updated >= -5m ORDER BY updated DESC";
        JiraSearchResponse resp = rest.search(jql, 0, 100);
        if (resp == null || resp.issues() == null) return List.of();
        return resp.issues().stream()
                .map(i -> JiraIssueMapper.toRef(props.id(), i))
                .toList();
    }

    @Override
    public ActionResult applyAction(ActionRequest request) {
        return switch (request.type()) {
            case "assign" -> {
                Object accountId = request.parameters().get("accountId");
                if (accountId == null) {
                    yield ActionResult.failure("missing accountId");
                }
                rest.assign(request.target().externalId(), accountId.toString());
                yield ActionResult.ok(request.target().externalId());
            }
            default -> ActionResult.failure("unsupported action: " + request.type());
        };
    }
}
