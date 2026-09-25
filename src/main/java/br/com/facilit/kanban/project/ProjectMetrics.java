package br.com.facilit.kanban.project;

public record ProjectMetrics(
        ProjectStatus status,
        long delayDays,
        double remainingTimePercentage
) {
}