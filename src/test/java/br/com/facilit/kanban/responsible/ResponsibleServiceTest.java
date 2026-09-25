package br.com.facilit.kanban.responsible;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.facilit.kanban.shared.ConflictException;
import br.com.facilit.kanban.shared.ResourceNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResponsibleServiceTest {

    @Mock
    private ResponsibleRepository repository;

    private ResponsibleService service;

    @BeforeEach
    void setUp() {
        service = new ResponsibleService(repository);
    }

    @Test
    void shouldCreateResponsibleWithNormalizedEmail() {
        when(repository.existsByEmailIgnoreCase("ana@example.com")).thenReturn(false);
        when(repository.save(any(Responsible.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponsibleResponse response = service.create(
                new ResponsibleRequest("Ana Silva", " ANA@EXAMPLE.COM ", "Gerente"));

        assertThat(response.email()).isEqualTo("ana@example.com");
        assertThat(response.name()).isEqualTo("Ana Silva");
        verify(repository).save(any(Responsible.class));
    }

    @Test
    void shouldRejectDuplicatedEmail() {
        when(repository.existsByEmailIgnoreCase("ana@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.create(
                new ResponsibleRequest("Outra Ana", "ana@example.com", "Analista")))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Já existe um responsável com este e-mail.");
    }

    @Test
    void shouldReportMissingResponsible() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Responsável não encontrado.");
    }
}