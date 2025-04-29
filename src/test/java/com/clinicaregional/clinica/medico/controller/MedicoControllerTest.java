package com.clinicaregional.clinica.medico.controller;

import com.clinicaregional.clinica.controller.MedicoController;
import com.clinicaregional.clinica.dto.request.MedicoRequestDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponseDTO;
import com.clinicaregional.clinica.enums.TipoContrato;
import com.clinicaregional.clinica.enums.TipoMedico;
import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.MedicoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MedicoController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthFilter.class, JwtUtil.class })
})
class MedicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MedicoService medicoService;

    @Autowired
    private ObjectMapper objectMapper;

    private MedicoResponseDTO medicoResponseDTO;
    private MedicoRequestDTO medicoRequestDTO;

    @BeforeEach
    void setUp() {
        medicoResponseDTO = new MedicoResponseDTO(
                1L, "Juan", "Pérez", null, null,
                "987654321", "Calle Salud 123", "Cardiólogo general", "imagen.jpg",
                LocalDateTime.now(), TipoContrato.FIJO, TipoMedico.ESPECIALISTA);

        medicoRequestDTO = new MedicoRequestDTO(
                "Juan", "Pérez", null, null,
                "987654321", "Calle Salud 123", "Cardiólogo general", "imagen.jpg",
                LocalDateTime.now(), TipoContrato.FIJO, TipoMedico.ESPECIALISTA, null);
    }

    @Test
    @DisplayName("Listar médicos debe retornar 200 OK")
    void listarMedicos() throws Exception {
        // Arrange
        when(medicoService.obtenerMedicos()).thenReturn(List.of(medicoResponseDTO));

        // Act & Assert
        mockMvc.perform(get("/api/medicos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombres").value("Juan"));
    }

    @Test
    @DisplayName("Crear nuevo médico debe retornar 201 Created")
    void crearMedico() throws Exception {
        // Arrange
        when(medicoService.guardarMedico(any())).thenReturn(medicoResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/medicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medicoRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombres").value("Juan"));
    }

    @Test
    @DisplayName("Crear médico con datos inválidos debe retornar 400 Bad Request")
    void crearMedico_datosInvalidos() throws Exception {
        // Arrange
        MedicoRequestDTO requestInvalido = new MedicoRequestDTO(
                "", "", null, null, "", "", "", "",
                LocalDateTime.now(), TipoContrato.FIJO, TipoMedico.GENERAL, null);

        // Act & Assert
        mockMvc.perform(post("/api/medicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Actualizar médico existente debe retornar 200 OK")
    void actualizarMedico() throws Exception {
        // Arrange
        when(medicoService.actualizarMedico(eq(1L), any())).thenReturn(medicoResponseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/medicos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medicoRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombres").value("Juan"));
    }

    @Test
    @DisplayName("Actualizar médico inexistente debe retornar 404 Not Found")
    void actualizarMedico_noExistente() throws Exception {
        // Arrange
        when(medicoService.actualizarMedico(eq(99L), any()))
                .thenThrow(new RuntimeException("Médico no encontrado"));

        // Act & Assert
        mockMvc.perform(put("/api/medicos/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medicoRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Eliminar médico existente debe retornar 204 No Content")
    void eliminarMedico() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/medicos/1"))
                .andExpect(status().isNoContent());
    }
}
