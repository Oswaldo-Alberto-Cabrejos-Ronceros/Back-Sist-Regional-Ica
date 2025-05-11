package com.clinicaregional.clinica.seguroCobertura.controller;

import com.clinicaregional.clinica.controller.SeguroCoberturaController;
import com.clinicaregional.clinica.dto.SeguroCoberturaDTO;
import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.SeguroCoberturaService;
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

@WebMvcTest(controllers = SeguroCoberturaController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthFilter.class, JwtUtil.class })
})
class SeguroCoberturaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SeguroCoberturaService seguroCoberturaService;

    @Autowired
    private ObjectMapper objectMapper;

    private SeguroCoberturaDTO dto;

    @BeforeEach
    void setUp() {
        dto = new SeguroCoberturaDTO(10L, 1L, 2L);
    }

    @Test
    @DisplayName("GET /api/seguro-coberturas debe listar todas las relaciones")
    void listarSeguroCobertura() throws Exception {
        // Arrange
        when(seguroCoberturaService.listarSeguroCobertura()).thenReturn(List.of(dto));

        // Act + Assert
        mockMvc.perform(get("/api/seguro-coberturas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L));
    }

    @Test
    @DisplayName("GET /api/seguro-coberturas/seguro/1 debe listar por seguroId")
    void listarPorSeguro() throws Exception {
        when(seguroCoberturaService.listarPorSeguro(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/seguro-coberturas/seguro/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seguroId").value(1L));
    }

    @Test
    @DisplayName("GET /api/seguro-coberturas/cobertura/2 debe listar por coberturaId")
    void listarPorCobertura() throws Exception {
        when(seguroCoberturaService.listarPorCobertura(2L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/seguro-coberturas/cobertura/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].coberturaId").value(2L));
    }

    @Test
    @DisplayName("GET /api/seguro-coberturas/10 debe retornar relación por ID")
    void getSeguroCobertura_existente() throws Exception {
        when(seguroCoberturaService.getSeguroCoberturaById(10L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/seguro-coberturas/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    @DisplayName("GET /api/seguro-coberturas/999 debe retornar 404 si no existe")
    void getSeguroCobertura_inexistente() throws Exception {
        when(seguroCoberturaService.getSeguroCoberturaById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/seguro-coberturas/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/seguro-coberturas debe crear nueva relación")
    void createSeguroCobertura() throws Exception {
        when(seguroCoberturaService.createSeguroCobertura(any())).thenReturn(dto);

        mockMvc.perform(post("/api/seguro-coberturas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    @DisplayName("DELETE /api/seguro-coberturas/10 debe eliminar relación")
    void deleteSeguroCobertura() throws Exception {
        mockMvc.perform(delete("/api/seguro-coberturas/10"))
                .andExpect(status().isNoContent());
    }
}
