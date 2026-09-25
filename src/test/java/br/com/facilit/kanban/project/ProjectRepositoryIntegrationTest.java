package br.com.facilit.kanban.project;

import br.com.facilit.kanban.KanbanApiApplication;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@org.springframework.test.context.ContextConfiguration(classes = KanbanApiApplication.class)
class ProjectRepositoryIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired private ProjectRepository repository;

    @Test
    void shouldPersistAndFilterProjectByStatus() {
        Project project = new Project(UUID.randomUUID(), "Projeto persistido");
        project.applyMetrics(new ProjectMetrics(ProjectStatus.NOT_STARTED, 0, 0));
        repository.saveAndFlush(project);

        assertThat(repository.findByStatus(ProjectStatus.NOT_STARTED,
                org.springframework.data.domain.Pageable.unpaged()).getContent()).hasSize(1);
    }
}