package br.com.facilit.kanban.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

import br.com.facilit.kanban.responsible.Responsible;
import br.com.facilit.kanban.responsible.ResponsibleRepository;
import br.com.facilit.kanban.shared.BusinessRuleException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 9, 23);

    @Mock private ProjectRepository repository;
    @Mock private ResponsibleRepository responsibleRepository;
    private ProjectService service;
    private UUID responsibleId;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-23T12:00:00Z"), ZoneOffset.UTC);
        service = new ProjectService(repository, responsibleRepository, new ProjectMetricsCalculator(), clock);
        responsibleId = UUID.randomUUID();
        lenient().when(repository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldCreateProjectWithCalculatedStatusAndMetrics() {
        when(responsibleRepository.findAllById(Set.of(responsibleId)))
                .thenReturn(List.of(new Responsible(responsibleId, "Ana", "ana@example.com", "Gerente")));
        ProjectRequest request = new ProjectRequest("Entrega", Set.of(responsibleId),
                TODAY.minusDays(5), TODAY.plusDays(5), TODAY.minusDays(4), null);

        ProjectResponse response = service.create(request);

        assertThat(response.status()).isEqualTo(ProjectStatus.IN_PROGRESS);
        assertThat(response.remainingTimePercentage()).isEqualTo(50.0);
    }

    @Test
    void shouldStartNotStartedProjectAutomatically() {
        Project project = project(TODAY, TODAY.plusDays(10), null, null);
        when(repository.findById(project.getId())).thenReturn(Optional.of(project));

        ProjectResponse response = service.transition(project.getId(), ProjectStatus.IN_PROGRESS);

        assertThat(response.actualStartDate()).isEqualTo(TODAY);
        assertThat(response.status()).isEqualTo(ProjectStatus.IN_PROGRESS);
    }

    @Test
    void shouldCompleteDelayedProjectAutomatically() {
        Project project = project(TODAY.minusDays(10), TODAY.minusDays(2), TODAY.minusDays(9), null);
        when(repository.findById(project.getId())).thenReturn(Optional.of(project));

        ProjectResponse response = service.transition(project.getId(), ProjectStatus.COMPLETED);

        assertThat(response.actualEndDate()).isEqualTo(TODAY);
        assertThat(response.status()).isEqualTo(ProjectStatus.COMPLETED);
        assertThat(response.delayDays()).isZero();
    }

    @Test
    void shouldBlockCompletedToInProgressWhenDatesAreOverdue() {
        Project project = project(TODAY.minusDays(10), TODAY.minusDays(2), TODAY.minusDays(9), TODAY.minusDays(1));
        when(repository.findById(project.getId())).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> service.transition(project.getId(), ProjectStatus.IN_PROGRESS))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("fica atrasado");
    }

    private Project project(LocalDate plannedStart, LocalDate plannedEnd,
                            LocalDate actualStart, LocalDate actualEnd) {
        Project project = new Project(UUID.randomUUID(), "Projeto");
        project.update("Projeto", Set.of(), plannedStart, plannedEnd, actualStart, actualEnd);
        project.applyMetrics(new ProjectMetricsCalculator().calculate(project, TODAY));
        return project;
    }
}