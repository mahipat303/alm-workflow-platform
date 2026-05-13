package com.almflow.app.api;

import com.almflow.engine.store.WorkflowRepository;
import com.almflow.engine.workflow.Workflow;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/workflows")
@CrossOrigin(origins = "*")
public class WorkflowController {

    private final WorkflowRepository repository;

    public WorkflowController(WorkflowRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Workflow> list() {
        return repository.all();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workflow> get(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Workflow> save(@PathVariable String id, @RequestBody Workflow workflow) {
        if (!id.equals(workflow.id())) {
            return ResponseEntity.badRequest().build();
        }
        repository.save(workflow);
        return ResponseEntity.ok(workflow);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        repository.delete(id);
        return ResponseEntity.noContent().build();
    }
}
