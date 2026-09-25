package br.com.facilit.kanban.responsible;

import br.com.facilit.kanban.shared.ConflictException;
import br.com.facilit.kanban.shared.ResourceNotFoundException;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResponsibleService {

    private final ResponsibleRepository repository;

    public ResponsibleService(ResponsibleRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ResponsibleResponse create(ResponsibleRequest request) {
        String email = normalizeEmail(request.email());
        ensureEmailAvailable(email, null);
        Responsible responsible = new Responsible(UUID.randomUUID(), request.name().trim(), email,
                request.jobTitle().trim());
        return ResponsibleResponse.from(repository.save(responsible));
    }

    @Transactional(readOnly = true)
    public Page<ResponsibleResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(ResponsibleResponse::from);
    }

    @Transactional(readOnly = true)
    public ResponsibleResponse findById(UUID id) {
        return ResponsibleResponse.from(findEntity(id));
    }

    @Transactional
    public ResponsibleResponse update(UUID id, ResponsibleRequest request) {
        Responsible responsible = findEntity(id);
        String email = normalizeEmail(request.email());
        ensureEmailAvailable(email, id);
        responsible.update(request.name().trim(), email, request.jobTitle().trim());
        return ResponsibleResponse.from(repository.save(responsible));
    }

    @Transactional
    public void delete(UUID id) {
        Responsible responsible = findEntity(id);
        repository.delete(responsible);
    }

    private Responsible findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado."));
    }

    private void ensureEmailAvailable(String email, UUID currentId) {
        boolean exists = currentId == null
                ? repository.existsByEmailIgnoreCase(email)
                : repository.existsByEmailIgnoreCaseAndIdNot(email, currentId);
        if (exists) {
            throw new ConflictException("Já existe um responsável com este e-mail.");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}