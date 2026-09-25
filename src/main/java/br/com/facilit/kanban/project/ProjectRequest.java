package br.com.facilit.kanban.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record ProjectRequest(
        @NotBlank @Size(max = 180) String name,
        @NotEmpty Set<UUID> responsibleIds,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate,
        LocalDate actualStartDate,
        LocalDate actualEndDate
) {
}