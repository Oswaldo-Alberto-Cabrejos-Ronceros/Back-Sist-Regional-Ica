package com.clinicaregional.clinica.cobertura.controller;

import com.clinicaregional.clinica.controller.CoberturaController;
import com.clinicaregional.clinica.dto.CoberturaDTO;
import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.CoberturaService;
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

@WebMvcTest(controllers = CoberturaController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthFilter.class, JwtUtil.class })
})
class CoberturaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CoberturaService coberturaService;

    @Autowired
    private ObjectMapper objectMapper;

    private CoberturaDTO coberturaDTO;

    @BeforeEach
    void setUp() {
        coberturaDTO = new CoberturaDTO(1L, "Cobertura Básica", "Cubre atenciones generales");
    }

    @Test
    @DisplayName("GET /api/coberturas debe retornar lista de coberturas")
    void listarCoberturas() throws Exception {
        // Arrange
        when(coberturaService.listarCoberturas()).thenReturn(List.of(coberturaDTO));

        // Act + Assert
        mockMvc.perform(get("/api/coberturas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Cobertura Básica"));
    }

    @Test
    @DisplayName("GET /api/coberturas/{id} debe retornar cobertura existente")
    void getCoberturaById_existente() throws Exception {
        // Arrange
        when(coberturaService.getCoberturaById(1L)).thenReturn(Optional.of(coberturaDTO));

        // Act + Assert
        mockMvc.perform(get("/api/coberturas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value("Cubre atenciones generales"));
    }

    @Test
    @DisplayName("GET /api/coberturas/{id} con ID inexistente debe retornar 404")
    void getCoberturaById_inexistente() throws Exception {
        // Arrange
        when(coberturaService.getCoberturaById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        mockMvc.perform(get("/api/coberturas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/coberturas debe crear una nueva cobertura")
    void createCobertura() throws Exception {
        // Arrange
        when(coberturaService.createCobertura(any())).thenReturn(coberturaDTO);

        // Act + Assert
        mockMvc.perform(post("/api/coberturas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coberturaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Cobertura Básica"));
    }

    @Test
    @DisplayName("PUT /api/coberturas/{id} debe actualizar cobertura")
    void updateCobertura() throws Exception {
        // Arrange
        when(coberturaService.updateCobertura(any(), any())).thenReturn(coberturaDTO);

        // Act + Assert
        mockMvc.perform(put("/api/coberturas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coberturaDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Cobertura Básica"));
    }

    @Test
    @DisplayName("DELETE /api/coberturas/{id} debe eliminar cobertura")
    void deleteCobertura() throws Exception {
        // Act + Assert
        mockMvc.perform(delete("/api/coberturas/1"))
                .andExpect(status().isNoContent());
    }
}
