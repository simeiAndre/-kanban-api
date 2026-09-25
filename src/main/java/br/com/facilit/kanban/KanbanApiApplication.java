package br.com.facilit.kanban;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class KanbanApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(KanbanApiApplication.class, args);
    }
}
