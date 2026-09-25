package br.com.facilit.kanban.project;

import br.com.facilit.kanban.responsible.Responsible;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "projects")
@EntityListeners(AuditingEntityListener.class)
public class Project {

    @Id
    private UUID id;

    @Column(nullable = false, length = 180)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "project_responsibles",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "responsible_id"))
    private Set<Responsible> responsibles = new LinkedHashSet<>();

    @Column(name = "planned_start_date")
    private LocalDate plannedStartDate;

    @Column(name = "planned_end_date")
    private LocalDate plannedEndDate;

    @Column(name = "actual_start_date")
    private LocalDate actualStartDate;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    @Column(name = "delay_days", nullable = false)
    private long delayDays;

    @Column(name = "remaining_time_percentage", nullable = false)
    private double remainingTimePercentage;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Project() {
    }

    public Project(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.status = ProjectStatus.NOT_STARTED;
    }

    public void update(String name, Set<Responsible> responsibles,
                       LocalDate plannedStartDate, LocalDate plannedEndDate,
                       LocalDate actualStartDate, LocalDate actualEndDate) {
        this.name = name;
        this.responsibles = new LinkedHashSet<>(responsibles);
        this.plannedStartDate = plannedStartDate;
        this.plannedEndDate = plannedEndDate;
        this.actualStartDate = actualStartDate;
        this.actualEndDate = actualEndDate;
    }

    public void applyMetrics(ProjectMetrics metrics) {
        this.status = metrics.status();
        this.delayDays = metrics.delayDays();
        this.remainingTimePercentage = metrics.remainingTimePercentage();
    }

    public void setActualStartDate(LocalDate actualStartDate) { this.actualStartDate = actualStartDate; }
    public void setActualEndDate(LocalDate actualEndDate) { this.actualEndDate = actualEndDate; }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public ProjectStatus getStatus() { return status; }
    public Set<Responsible> getResponsibles() { return Set.copyOf(responsibles); }
    public LocalDate getPlannedStartDate() { return plannedStartDate; }
    public LocalDate getPlannedEndDate() { return plannedEndDate; }
    public LocalDate getActualStartDate() { return actualStartDate; }
    public LocalDate getActualEndDate() { return actualEndDate; }
    public long getDelayDays() { return delayDays; }
    public double getRemainingTimePercentage() { return remainingTimePercentage; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}