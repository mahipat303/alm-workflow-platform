package com.almflow.connector.jira.internal;

import com.almflow.connector.jira.JiraConnectorProperties;
import com.almflow.connector.jira.dto.JiraIssueDto;
import com.almflow.connector.jira.dto.JiraSearchResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * Thin wrapper over Jira Cloud REST v3. Pure HTTP — no domain mapping here;
 * mapping to {@code core} types is the connector's responsibility.
 */
public class JiraRestClient {

    private final RestClient http;

    public JiraRestClient(JiraConnectorProperties props) {
        String basic = Base64.getEncoder().encodeToString(
                (props.email() + ":" + props.apiToken()).getBytes(StandardCharsets.UTF_8));
        this.http = RestClient.builder()
                .baseUrl(props.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + basic)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public JiraIssueDto getIssue(String key, boolean expandChangelog) {
        String path = "/rest/api/3/issue/" + key + (expandChangelog ? "?expand=changelog" : "");
        return http.get().uri(path).retrieve().body(JiraIssueDto.class);
    }

    public JiraSearchResponse search(String jql, int startAt, int maxResults) {
        return http.get()
                .uri(uri -> uri.path("/rest/api/3/search")
                        .queryParam("jql", jql)
                        .queryParam("startAt", startAt)
                        .queryParam("maxResults", maxResults)
                        .queryParam("fields", "summary,status,priority,assignee,updated")
                        .queryParam("expand", "changelog")
                        .build())
                .retrieve()
                .body(JiraSearchResponse.class);
    }

    public void assign(String key, String accountId) {
        http.put()
                .uri("/rest/api/3/issue/" + key + "/assignee")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("accountId", accountId))
                .retrieve()
                .toBodilessEntity();
    }
}
