package br.com.facilit.kanban.project;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProjectMetricsCalculatorTest {

    private final ProjectMetricsCalculator calculator = new ProjectMetricsCalculator();
    private final LocalDate today = LocalDate.of(2026, 9, 23);

    @Test
    void shouldClassifyFutureProjectAsNotStartedAndCalculateRemainingTime() {
        Project project = project(today.plusDays(2), today.plusDays(12), null, null);

        ProjectMetrics metrics = calculator.calculate(project, today);

        assertThat(metrics.status()).isEqualTo(ProjectStatus.NOT_STARTED);
        assertThat(metrics.delayDays()).isZero();
        assertThat(metrics.remainingTimePercentage()).isEqualTo(100.0);
    }

    @Test
    void shouldClassifyStartedProjectAsInProgress() {
        Project project = project(today.minusDays(5), today.plusDays(5), today.minusDays(4), null);

        ProjectMetrics metrics = calculator.calculate(project, today);

        assertThat(metrics.status()).isEqualTo(ProjectStatus.IN_PROGRESS);
        assertThat(metrics.remainingTimePercentage()).isEqualTo(50.0);
    }

    @Test
    void shouldClassifyOverdueProjectAndCalculateDelay() {
        Project project = project(today.minusDays(10), today.minusDays(3), today.minusDays(9), null);

        ProjectMetrics metrics = calculator.calculate(project, today);

        assertThat(metrics.status()).isEqualTo(ProjectStatus.DELAYED);
        assertThat(metrics.delayDays()).isEqualTo(3);
        assertThat(metrics.remainingTimePercentage()).isZero();
    }

    @Test
    void shouldClassifyFinishedProjectAndClearMetrics() {
        Project project = project(today.minusDays(10), today.minusDays(2), today.minusDays(9), today.minusDays(1));

        ProjectMetrics metrics = calculator.calculate(project, today);

        assertThat(metrics.status()).isEqualTo(ProjectStatus.COMPLETED);
        assertThat(metrics.delayDays()).isZero();
        assertThat(metrics.remainingTimePercentage()).isZero();
    }

    @Test
    void shouldReturnZeroForInvalidPlannedInterval() {
        Project project = project(today, today, null, null);

        assertThat(calculator.calculate(project, today).remainingTimePercentage()).isZero();
    }

    private Project project(LocalDate plannedStart, LocalDate plannedEnd,
                            LocalDate actualStart, LocalDate actualEnd) {
        Project project = new Project(UUID.randomUUID(), "Projeto");
        project.update("Projeto", Set.of(), plannedStart, plannedEnd, actualStart, actualEnd);
        return project;
    }
}