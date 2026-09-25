package br.com.facilit.kanban.project;

import br.com.facilit.kanban.KanbanApiApplication;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProjectController.class)
@org.springframework.test.context.ContextConfiguration(classes = KanbanApiApplication.class)
class ProjectControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private ProjectService service;
    @MockBean private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void shouldFilterKanbanByStatus() throws Exception {
        when(service.list(eq(ProjectStatus.DELAYED), any())).thenReturn(new PageImpl<>(java.util.List.of()));

        mockMvc.perform(get("/api/projects").param("status", "DELAYED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldRejectInvalidProjectPayload() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType("application/json")
                        .content("{\"name\":\"\",\"responsibleIds\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados de entrada inválidos."));
    }
}