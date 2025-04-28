package com.clinicaregional.clinica.especialidades.controller;

import com.clinicaregional.clinica.controller.EspecialidadController;
import com.clinicaregional.clinica.dto.request.EspecialidadRequest;
import com.clinicaregional.clinica.dto.response.EspecialidadResponse;
import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.EspecialidadService;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EspecialidadController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthFilter.class, JwtUtil.class })
})
class EspecialidadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EspecialidadService especialidadService;

    @Autowired
    private ObjectMapper objectMapper;

    private EspecialidadResponse especialidadResponse;
    private EspecialidadRequest especialidadRequest;

    @BeforeEach
    void setUp() {
        especialidadResponse = new EspecialidadResponse(1L, "Cardiología", "Problemas del corazón", "cardio.jpg");
        especialidadRequest = new EspecialidadRequest("Cardiología", "Problemas del corazón", "cardio.jpg");
    }

    @Test
    @DisplayName("Listar todas las especialidades debe retornar 200 OK")
    void listarEspecialidades() throws Exception {
        // Arrange
        when(especialidadService.listarEspecialidades()).thenReturn(List.of(especialidadResponse));

        // Act & Assert
        mockMvc.perform(get("/api/especialidades"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombre").value("Cardiología"));
    }

    @Test
    @DisplayName("Obtener especialidad por ID existente debe retornar 200 OK")
    void obtenerEspecialidadPorId_existente() throws Exception {
        // Arrange
        when(especialidadService.getEspecialidadById(1L)).thenReturn(Optional.of(especialidadResponse));

        // Act & Assert
        mockMvc.perform(get("/api/especialidades/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre").value("Cardiología"));
    }

    @Test
    @DisplayName("Obtener especialidad por ID inexistente debe retornar 404 Not Found")
    void obtenerEspecialidadPorId_noExistente() throws Exception {
        // Arrange
        when(especialidadService.getEspecialidadById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/especialidades/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Crear nueva especialidad correctamente debe retornar 201 Created")
    void crearEspecialidad() throws Exception {
        // Arrange
        when(especialidadService.guardarEspecialidad(any(EspecialidadRequest.class))).thenReturn(especialidadResponse);

        // Act & Assert
        mockMvc.perform(post("/api/especialidades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(especialidadRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Cardiología"));
    }

    @Test
    @DisplayName("Crear especialidad con datos inválidos debe retornar 400 Bad Request")
    void crearEspecialidad_datosInvalidos() throws Exception {
        // Arrange
        EspecialidadRequest requestInvalido = new EspecialidadRequest("", "", ""); // nombre y descripcion vacíos

        // Act & Assert
        mockMvc.perform(post("/api/especialidades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Actualizar especialidad existente debe retornar 200 OK")
    void actualizarEspecialidad() throws Exception {
        // Arrange
        when(especialidadService.actualizarEspecialidad(eq(1L), any(EspecialidadRequest.class)))
                .thenReturn(especialidadResponse);

        // Act & Assert
        mockMvc.perform(put("/api/especialidades/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(especialidadRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Cardiología"));
    }

    @Test
    @DisplayName("Actualizar especialidad inexistente debe retornar 404 Not Found")
    void actualizarEspecialidad_noExistente() throws Exception {
        // Arrange
        when(especialidadService.actualizarEspecialidad(eq(99L), any(EspecialidadRequest.class)))
                .thenThrow(new RuntimeException("Especialidad no encontrada"));

        // Act & Assert
        mockMvc.perform(put("/api/especialidades/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(especialidadRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Eliminar especialidad existente debe retornar 204 No Content")
    void eliminarEspecialidad() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/especialidades/1"))
                .andExpect(status().isNoContent());
    }
}