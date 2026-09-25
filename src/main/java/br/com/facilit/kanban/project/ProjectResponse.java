package br.com.facilit.kanban.project;

import br.com.facilit.kanban.responsible.ResponsibleResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record ProjectResponse(
        UUID id,
        String name,
        ProjectStatus status,
        Set<ResponsibleResponse> responsibles,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate,
        LocalDate actualStartDate,
        LocalDate actualEndDate,
        long delayDays,
        double remainingTimePercentage,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectResponse from(Project project) {
        Set<ResponsibleResponse> responsibles = project.getResponsibles().stream()
                .map(ResponsibleResponse::from)
                .collect(Collectors.toSet());
        return new ProjectResponse(project.getId(), project.getName(), project.getStatus(), responsibles,
                project.getPlannedStartDate(), project.getPlannedEndDate(), project.getActualStartDate(),
                project.getActualEndDate(), project.getDelayDays(), project.getRemainingTimePercentage(),
                project.getCreatedAt(), project.getUpdatedAt());
    }
}