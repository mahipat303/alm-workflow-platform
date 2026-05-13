package com.almflow.engine.store;

import com.almflow.engine.workflow.Workflow;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Reads workflow JSON files from a directory. Adequate for v1 — once the UI
 * is in place this gets replaced with a database-backed repository.
 */
public class FileWorkflowRepository implements WorkflowRepository {

    private static final Logger log = LoggerFactory.getLogger(FileWorkflowRepository.class);

    private final Path dir;
    private final ObjectMapper mapper;

    public FileWorkflowRepository(Path dir, ObjectMapper mapper) {
        this.dir = dir;
        this.mapper = mapper;
    }

    @Override
    public List<Workflow> all() {
        if (!Files.isDirectory(dir)) {
            log.warn("workflow dir {} does not exist; no workflows loaded", dir);
            return List.of();
        }
        List<Workflow> out = new ArrayList<>();
        try (Stream<Path> files = Files.list(dir)) {
            files.filter(p -> p.toString().endsWith(".json")).forEach(p -> {
                try {
                    out.add(mapper.readValue(p.toFile(), Workflow.class));
                } catch (IOException e) {
                    log.error("failed to load workflow {}", p, e);
                }
            });
        } catch (IOException e) {
            log.error("failed to list workflow dir {}", dir, e);
        }
        return out;
    }

    @Override
    public List<Workflow> forTrigger(String connectorId, String entityType) {
        return all().stream()
                .filter(Workflow::enabled)
                .filter(w -> w.trigger() != null)
                .filter(w -> w.trigger().connectorId() == null
                        || w.trigger().connectorId().equals(connectorId))
                .filter(w -> w.trigger().entityType() == null
                        || w.trigger().entityType().equals(entityType))
                .toList();
    }
}
