package br.com.facilit.kanban.responsible;

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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/responsibles")
@Tag(name = "Responsáveis", description = "Cadastro e manutenção dos responsáveis pelos projetos")
public class ResponsibleController {

    private final ResponsibleService service;

    public ResponsibleController(ResponsibleService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastrar responsável")
    public ResponseEntity<ResponsibleResponse> create(@Valid @RequestBody ResponsibleRequest request) {
        ResponsibleResponse response = service.create(request);
        return ResponseEntity.created(URI.create("/api/responsibles/" + response.id())).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar responsáveis com paginação")
    public Page<ResponsibleResponse> list(Pageable pageable) {
        return service.list(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar responsável")
    public ResponsibleResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar responsável")
    public ResponsibleResponse update(@PathVariable UUID id,
                                      @Valid @RequestBody ResponsibleRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir responsável")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}