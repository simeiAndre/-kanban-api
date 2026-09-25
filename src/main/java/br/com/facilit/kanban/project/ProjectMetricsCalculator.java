package br.com.facilit.kanban.project;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class ProjectMetricsCalculator {

    public ProjectMetrics calculate(Project project, LocalDate today) {
        ProjectStatus status = determineStatus(project, today);
        return new ProjectMetrics(status, calculateDelayDays(project, status, today),
                calculateRemainingPercentage(project, status, today));
    }

    ProjectStatus determineStatus(Project project, LocalDate today) {
        if (project.getActualEndDate() != null) {
            return ProjectStatus.COMPLETED;
        }
        if ((project.getActualStartDate() == null && isBefore(project.getPlannedStartDate(), today))
                || isBefore(project.getPlannedEndDate(), today)) {
            return ProjectStatus.DELAYED;
        }
        if (project.getActualStartDate() != null
                && project.getPlannedEndDate() != null
                && project.getPlannedEndDate().isAfter(today)) {
            return ProjectStatus.IN_PROGRESS;
        }
        return ProjectStatus.NOT_STARTED;
    }

    private long calculateDelayDays(Project project, ProjectStatus status, LocalDate today) {
        if (status == ProjectStatus.COMPLETED || project.getPlannedEndDate() == null
                || !project.getPlannedEndDate().isBefore(today)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(project.getPlannedEndDate(), today);
    }

    private double calculateRemainingPercentage(Project project, ProjectStatus status, LocalDate today) {
        if (status == ProjectStatus.COMPLETED
                || project.getPlannedStartDate() == null || project.getPlannedEndDate() == null) {
            return 0;
        }
        long total = ChronoUnit.DAYS.between(project.getPlannedStartDate(), project.getPlannedEndDate());
        if (total <= 0) {
            return 0;
        }
        long used = ChronoUnit.DAYS.between(project.getPlannedStartDate(), today);
        long remaining = Math.max(0, Math.min(total, total - used));
        return Math.round((remaining * 10000.0) / total) / 100.0;
    }

    private boolean isBefore(LocalDate date, LocalDate today) {
        return date != null && date.isBefore(today);
    }
}