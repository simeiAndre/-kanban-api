package br.com.facilit.kanban.project;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @Override
    @EntityGraph(attributePaths = "responsibles")
    Page<Project> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "responsibles")
    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);
}