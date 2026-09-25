package br.com.facilit.kanban.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    OpenAPI kanbanOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Kanban API")
                .version("1.0.0")
                .description("API REST para projetos, responsáveis, métricas e transições Kanban.")
                .contact(new Contact().name("Desafio Técnico Backend")));
    }
}