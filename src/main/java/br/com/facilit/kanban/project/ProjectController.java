package br.com.facilit.kanban.project;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projetos", description = "Projetos, métricas e movimentações do Kanban")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastrar projeto", description = "Calcula status, atraso e percentual restante automaticamente.")
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        ProjectResponse response = service.create(request);
        return ResponseEntity.created(URI.create("/api/projects/" + response.id())).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar projetos", description = "Use status para obter uma coluna específica do Kanban.")
    public Page<ProjectResponse> list(@RequestParam(required = false) ProjectStatus status, Pageable pageable) {
        return service.list(status, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar projeto")
    public ProjectResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar projeto e recalcular métricas")
    public ProjectResponse update(@PathVariable UUID id, @Valid @RequestBody ProjectRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir projeto")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/transitions/{targetStatus}")
    @Operation(summary = "Mover projeto no Kanban", description = "Aplica efeitos automáticos e valida a tabela de transições.")
    public ProjectResponse transition(@PathVariable UUID id, @PathVariable ProjectStatus targetStatus) {
        return service.transition(id, targetStatus);
    }
}