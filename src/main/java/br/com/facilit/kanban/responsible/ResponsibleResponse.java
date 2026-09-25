package br.com.facilit.kanban.responsible;

import java.time.Instant;
import java.util.UUID;

public record ResponsibleResponse(
        UUID id,
        String name,
        String email,
        String jobTitle,
        Instant createdAt,
        Instant updatedAt
) {
    public static ResponsibleResponse from(Responsible responsible) {
        return new ResponsibleResponse(
                responsible.getId(),
                responsible.getName(),
                responsible.getEmail(),
                responsible.getJobTitle(),
                responsible.getCreatedAt(),
                responsible.getUpdatedAt()
        );
    }
}