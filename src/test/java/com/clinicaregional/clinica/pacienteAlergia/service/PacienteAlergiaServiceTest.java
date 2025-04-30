package com.clinicaregional.clinica.pacienteAlergia.service;

import com.clinicaregional.clinica.dto.PacienteAlergiaDTO;
import com.clinicaregional.clinica.service.PacienteAlergiaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PacienteAlergiaServiceTest {

    @Mock
    private PacienteAlergiaService pacienteAlergiaService;

    private PacienteAlergiaDTO pacienteAlergiaDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        pacienteAlergiaDTO = new PacienteAlergiaDTO();
        pacienteAlergiaDTO.setId(1L);
    }

    @Test
    @DisplayName("Listar todas las relaciones Paciente-Alergia")
    void listarPacienteAlergias() {
        when(pacienteAlergiaService.listarPacienteAlergias()).thenReturn(List.of(pacienteAlergiaDTO));

        List<PacienteAlergiaDTO> resultado = pacienteAlergiaService.listarPacienteAlergias();

        assertThat(resultado).isNotEmpty();
        verify(pacienteAlergiaService, times(1)).listarPacienteAlergias();
    }

    @Test
    @DisplayName("Listar relaciones Paciente-Alergia por paciente ID")
    void listarPacienteAlergiasPorPaciente() {
        when(pacienteAlergiaService.listarPacienteAlergiasPorPaciente(1L)).thenReturn(List.of(pacienteAlergiaDTO));

        List<PacienteAlergiaDTO> resultado = pacienteAlergiaService.listarPacienteAlergiasPorPaciente(1L);

        assertThat(resultado).isNotEmpty();
        verify(pacienteAlergiaService, times(1)).listarPacienteAlergiasPorPaciente(1L);
    }

    @Test
    @DisplayName("Obtener PacienteAlergia por ID")
    void getPacienteAlergiaById() {
        when(pacienteAlergiaService.getPacienteAlergiaById(1L)).thenReturn(Optional.of(pacienteAlergiaDTO));

        Optional<PacienteAlergiaDTO> resultado = pacienteAlergiaService.getPacienteAlergiaById(1L);

        assertThat(resultado).isPresent();
        verify(pacienteAlergiaService, times(1)).getPacienteAlergiaById(1L);
    }

    @Test
    @DisplayName("Crear una nueva PacienteAlergia")
    void createPacienteAlergia() {
        when(pacienteAlergiaService.createPacienteAlergia(any())).thenReturn(pacienteAlergiaDTO);

        PacienteAlergiaDTO resultado = pacienteAlergiaService.createPacienteAlergia(pacienteAlergiaDTO);

        assertThat(resultado).isNotNull();
        verify(pacienteAlergiaService, times(1)).createPacienteAlergia(any());
    }

    @Test
    @DisplayName("Actualizar una PacienteAlergia")
    void updatePacienteAlergia() {
        when(pacienteAlergiaService.updatePacienteAlergia(eq(1L), any())).thenReturn(pacienteAlergiaDTO);

        PacienteAlergiaDTO resultado = pacienteAlergiaService.updatePacienteAlergia(1L, pacienteAlergiaDTO);

        assertThat(resultado).isNotNull();
        verify(pacienteAlergiaService, times(1)).updatePacienteAlergia(eq(1L), any());
    }

    @Test
    @DisplayName("Eliminar una PacienteAlergia")
    void deletePacienteAlergia() {
        doNothing().when(pacienteAlergiaService).deletePacienteAlergia(1L);

        pacienteAlergiaService.deletePacienteAlergia(1L);

        verify(pacienteAlergiaService, times(1)).deletePacienteAlergia(1L);
    }
}
