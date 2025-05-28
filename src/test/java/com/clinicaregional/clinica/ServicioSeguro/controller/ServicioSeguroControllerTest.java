package com.clinicaregional.clinica.ServicioSeguro.controller;

import com.clinicaregional.clinica.controller.ServicioSeguroController;
import com.clinicaregional.clinica.dto.ServicioSeguroDTO;
import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.ServicioSeguroService;
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

@WebMvcTest(controllers = ServicioSeguroController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {JwtAuthFilter.class, JwtUtil.class})
})
class ServicioSeguroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServicioSeguroService servicioSeguroService;

    @Autowired
    private ObjectMapper objectMapper;

    private ServicioSeguroDTO dto;

    @BeforeEach
    void setUp() {
        dto = new ServicioSeguroDTO(10L, 1L, 2L, 3L);
    }

    @Test
    @DisplayName("GET /api/servicios-seguros debe retornar lista")
    void listarServicioSeguro() throws Exception {
        when(servicioSeguroService.listarServicioSeguro()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/servicios-seguros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L));
    }

    @Test
    @DisplayName("GET /api/servicios-seguros/servicio/1 debe retornar por servicioId")
    void listarPorServicio() throws Exception {
        when(servicioSeguroService.listarPorServicio(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/servicios-seguros/servicio/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].servicioId").value(1L));
    }

    @Test
    @DisplayName("GET /api/servicios-seguros/seguro/2 debe retornar por seguroId")
    void listarPorSeguro() throws Exception {
        when(servicioSeguroService.listarPorSeguro(2L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/servicios-seguros/seguro/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seguroId").value(2L));
    }

    @Test
    @DisplayName("GET /api/servicios-seguros/cobertura/3 debe retornar por coberturaId")
    void listarPorCobertura() throws Exception {
        when(servicioSeguroService.listarPorCobertura(3L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/servicios-seguros/cobertura/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].coberturaId").value(3L));
    }

    @Test
    @DisplayName("GET /api/servicios-seguros/10 debe retornar por ID")
    void getSeguroServicioById_existente() throws Exception {
        when(servicioSeguroService.getSeguroServicioById(10L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/servicios-seguros/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    @DisplayName("GET /api/servicios-seguros/999 debe retornar 404 si no existe")
    void getSeguroServicioById_inexistente() throws Exception {
        when(servicioSeguroService.getSeguroServicioById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/servicios-seguros/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/servicios-seguros debe crear nuevo ServicioSeguro")
    void createServicioSeguro() throws Exception {
        when(servicioSeguroService.createServicioSeguro(any())).thenReturn(dto);

        mockMvc.perform(post("/api/servicios-seguros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    @DisplayName("DELETE /api/servicios-seguros/10 debe eliminar ServicioSeguro")
    void deleteServicioSeguro() throws Exception {
        mockMvc.perform(delete("/api/servicios-seguros/10"))
                .andExpect(status().isNoContent());
    }
}
