package com.clinicaregional.clinica.seguro.controller;

import com.clinicaregional.clinica.controller.SeguroController;
import com.clinicaregional.clinica.dto.EstadoSeguroDTO;
import com.clinicaregional.clinica.dto.SeguroDTO;
import com.clinicaregional.clinica.enums.EstadoSeguro;
import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.SeguroService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SeguroController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthFilter.class, JwtUtil.class })
})
class SeguroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SeguroService seguroService;

    @Autowired
    private ObjectMapper objectMapper;

    private SeguroDTO seguroDTO;

    @BeforeEach
    void setUp() {
        seguroDTO = new SeguroDTO(1L, "RIMAC", "Cobertura nacional", "rimac.jpg", EstadoSeguro.ACTIVO);
    }

    @Test
    @DisplayName("GET /api/seguros debe retornar lista de seguros")
    void listarSeguros() throws Exception {
        // Arrange
        when(seguroService.listarSeguros()).thenReturn(List.of(seguroDTO));

        // Act + Assert
        mockMvc.perform(get("/api/seguros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("RIMAC"));
    }

    @Test
    @DisplayName("GET /api/seguros/id/1 debe retornar seguro por ID")
    void getSeguroById_existente() throws Exception {
        // Arrange
        when(seguroService.getSeguroById(1L)).thenReturn(Optional.of(seguroDTO));

        // Act + Assert
        mockMvc.perform(get("/api/seguros/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("RIMAC"));
    }

    @Test
    @DisplayName("GET /api/seguros/id/99 debe retornar 404 si no existe")
    void getSeguroById_inexistente() throws Exception {
        // Arrange
        when(seguroService.getSeguroById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        mockMvc.perform(get("/api/seguros/id/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/seguros/nombre/RIMAC debe retornar seguro")
    void getSeguroByNombre_existente() throws Exception {
        // Arrange
        when(seguroService.getSeguroByNombre("RIMAC")).thenReturn(Optional.of(seguroDTO));

        // Act + Assert
        mockMvc.perform(get("/api/seguros/nombre/RIMAC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value("Cobertura nacional"));
    }

    @Test
    @DisplayName("POST /api/seguros debe crear nuevo seguro")
    void createSeguro() throws Exception {
        // Arrange
        when(seguroService.createSeguro(any())).thenReturn(seguroDTO);

        // Act + Assert
        mockMvc.perform(post("/api/seguros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seguroDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("RIMAC"));
    }

    @Test
    @DisplayName("PUT /api/seguros/1 debe actualizar seguro")
    void updateSeguro() throws Exception {
        // Arrange
        when(seguroService.updateSeguro(any(), any())).thenReturn(seguroDTO);

        // Act + Assert
        mockMvc.perform(put("/api/seguros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seguroDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imagenUrl").value("rimac.jpg"));
    }

    @Test
    @DisplayName("PATCH /api/seguros/estado-seguro/1 debe actualizar estado")
    void updateEstadoSeguro() throws Exception {
        // Arrange
        EstadoSeguroDTO estadoDTO = new EstadoSeguroDTO(EstadoSeguro.INACTIVO);
        when(seguroService.updateEstadoSeguro(1L, EstadoSeguro.INACTIVO)).thenReturn(seguroDTO);

        // Act + Assert
        mockMvc.perform(patch("/api/seguros/estado-seguro/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(estadoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoSeguro").value("ACTIVO")); // mocked response
    }

    @Test
    @DisplayName("DELETE /api/seguros/1 debe eliminar seguro")
    void deleteSeguro() throws Exception {
        // Act + Assert
        mockMvc.perform(delete("/api/seguros/1"))
                .andExpect(status().isNoContent());
    }
}
