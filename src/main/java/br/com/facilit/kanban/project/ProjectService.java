package br.com.facilit.kanban.project;

import br.com.facilit.kanban.responsible.Responsible;
import br.com.facilit.kanban.responsible.ResponsibleRepository;
import br.com.facilit.kanban.shared.BusinessRuleException;
import br.com.facilit.kanban.shared.ResourceNotFoundException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

    private final ProjectRepository repository;
    private final ResponsibleRepository responsibleRepository;
    private final ProjectMetricsCalculator calculator;
    private final Clock clock;

    public ProjectService(ProjectRepository repository, ResponsibleRepository responsibleRepository,
                          ProjectMetricsCalculator calculator) {
        this(repository, responsibleRepository, calculator, Clock.systemDefaultZone());
    }

    ProjectService(ProjectRepository repository, ResponsibleRepository responsibleRepository,
                   ProjectMetricsCalculator calculator, Clock clock) {
        this.repository = repository;
        this.responsibleRepository = responsibleRepository;
        this.calculator = calculator;
        this.clock = clock;
    }

    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        Project project = new Project(UUID.randomUUID(), request.name().trim());
        applyRequest(project, request);
        return ProjectResponse.from(repository.save(project));
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> list(ProjectStatus status, Pageable pageable) {
        Page<Project> projects = status == null ? repository.findAll(pageable)
                : repository.findByStatus(status, pageable);
        return projects.map(ProjectResponse::from);
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(UUID id) {
        return ProjectResponse.from(findEntity(id));
    }

    @Transactional
    public ProjectResponse update(UUID id, ProjectRequest request) {
        Project project = findEntity(id);
        applyRequest(project, request);
        return ProjectResponse.from(repository.save(project));
    }

    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    @Transactional
    public ProjectResponse transition(UUID id, ProjectStatus targetStatus) {
        Project project = findEntity(id);
        ProjectStatus currentStatus = calculator.calculate(project, today()).status();
        if (currentStatus == targetStatus) {
            throw new BusinessRuleException("O projeto já está no status solicitado.");
        }

        applyTransitionEffect(project, currentStatus, targetStatus);
        ProjectMetrics metrics = calculator.calculate(project, today());
        if (metrics.status() != targetStatus) {
            throw new BusinessRuleException(transitionGuidance(currentStatus, targetStatus));
        }
        project.applyMetrics(metrics);
        return ProjectResponse.from(repository.save(project));
    }

    private void applyRequest(Project project, ProjectRequest request) {
        validateDates(request);
        project.update(request.name().trim(), loadResponsibles(request.responsibleIds()),
                request.plannedStartDate(), request.plannedEndDate(),
                request.actualStartDate(), request.actualEndDate());
        project.applyMetrics(calculator.calculate(project, today()));
    }

    private void applyTransitionEffect(Project project, ProjectStatus from, ProjectStatus to) {
        LocalDate today = today();
        if (from == ProjectStatus.NOT_STARTED && to == ProjectStatus.IN_PROGRESS) {
            project.setActualStartDate(today);
        } else if ((from == ProjectStatus.NOT_STARTED || from == ProjectStatus.IN_PROGRESS
                || from == ProjectStatus.DELAYED) && to == ProjectStatus.COMPLETED) {
            project.setActualEndDate(today);
        } else if (from == ProjectStatus.IN_PROGRESS && to == ProjectStatus.NOT_STARTED) {
            project.setActualStartDate(null);
        } else if (from == ProjectStatus.COMPLETED
                && (to == ProjectStatus.IN_PROGRESS || to == ProjectStatus.DELAYED)) {
            project.setActualEndDate(null);
        }
    }

    private String transitionGuidance(ProjectStatus from, ProjectStatus to) {
        if (from == ProjectStatus.NOT_STARTED && to == ProjectStatus.DELAYED) {
            return "Não é possível marcar como atrasado antes da data prevista. Ajuste uma data prevista para antes de hoje.";
        }
        if (from == ProjectStatus.IN_PROGRESS && to == ProjectStatus.DELAYED) {
            return "Remova o início realizado ou ajuste o início/término previsto para uma data anterior a hoje.";
        }
        if (from == ProjectStatus.DELAYED && to == ProjectStatus.NOT_STARTED) {
            return "Remova o início realizado e ajuste as datas previstas para hoje ou uma data futura.";
        }
        if (from == ProjectStatus.DELAYED && to == ProjectStatus.IN_PROGRESS) {
            return "Preencha o início realizado e ajuste o término previsto para uma data posterior a hoje.";
        }
        if (from == ProjectStatus.COMPLETED && to == ProjectStatus.NOT_STARTED) {
            return "Remova o término realizado, remova o início realizado e ajuste as datas previstas para hoje ou uma data futura.";
        }
        if (from == ProjectStatus.COMPLETED && to == ProjectStatus.IN_PROGRESS) {
            return "Ao remover o término realizado o projeto fica atrasado. Ajuste o término previsto para depois de hoje.";
        }
        if (from == ProjectStatus.COMPLETED && to == ProjectStatus.DELAYED) {
            return "Ao remover o término realizado, as datas não classificam o projeto como atrasado. Ajuste uma data prevista para antes de hoje.";
        }
        return "A transição solicitada é incompatível com as datas do projeto. Ajuste as datas e tente novamente.";
    }

    private Set<Responsible> loadResponsibles(Set<UUID> ids) {
        Set<Responsible> responsibles = new LinkedHashSet<>(responsibleRepository.findAllById(ids));
        if (responsibles.size() != ids.size()) {
            throw new ResourceNotFoundException("Um ou mais responsáveis não foram encontrados.");
        }
        return responsibles;
    }

    private void validateDates(ProjectRequest request) {
        if (request.plannedStartDate() != null && request.plannedEndDate() != null
                && request.plannedEndDate().isBefore(request.plannedStartDate())) {
            throw new BusinessRuleException("O término previsto não pode ser anterior ao início previsto.");
        }
        if (request.actualStartDate() != null && request.actualEndDate() != null
                && request.actualEndDate().isBefore(request.actualStartDate())) {
            throw new BusinessRuleException("O término realizado não pode ser anterior ao início realizado.");
        }
    }

    private Project findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado."));
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }
}