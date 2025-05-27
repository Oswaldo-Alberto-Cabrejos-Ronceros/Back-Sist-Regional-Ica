package com.clinicaregional.clinica.medicoEspecialidad.controller;

import com.clinicaregional.clinica.controller.MedicoEspecialidadController;
import com.clinicaregional.clinica.dto.request.MedicoEspecialidadRequest;
import com.clinicaregional.clinica.dto.response.MedicoEspecialidadResponse;
import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.MedicoEspecialidadService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MedicoEspecialidadController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthFilter.class, JwtUtil.class })
})
class MedicoEspecialidadControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private MedicoEspecialidadService medicoEspecialidadService;
    @Autowired
    private ObjectMapper objectMapper;

    private MedicoEspecialidadRequest request;
    private MedicoEspecialidadResponse response;

    @BeforeEach
    void setUp() {
        request = new MedicoEspecialidadRequest(1L, 2L, LocalDate.now());
        response = new MedicoEspecialidadResponse(1L, "Luis Ramirez", "12345678901", "987654321", 2L, "Cardiologia",
                LocalDate.now());
    }

    @Test
    @DisplayName("Registrar relación médico-especialidad debe retornar 201")
    void registrarRelacionME() throws Exception {
        when(medicoEspecialidadService.registrarRelacionME(any())).thenReturn(response);

        mockMvc.perform(post("/api/medico-especialidad")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.medicoId").value(1));
    }

    @Test
    @DisplayName("Obtener todas las relaciones debe retornar 200")
    void obtenerTodasRelaciones() throws Exception {
        when(medicoEspecialidadService.obtenerTodasRelacionesME()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/medico-especialidad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicoId").value(1));
    }

    @Test
    @DisplayName("Actualizar relación debe retornar 200")
    void actualizarRelacionME() throws Exception {
        when(medicoEspecialidadService.actualizarRelacionME(any(), any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/medico-especialidad/1/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.especialidadId").value(2));
    }

    @Test
    @DisplayName("Eliminar relación debe retornar 204")
    void eliminarRelacionME() throws Exception {
        mockMvc.perform(delete("/api/medico-especialidad/1/2"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Obtener especialidades por médico debe retornar 200")
    void obtenerEspecialidadDelMedico() throws Exception {
        when(medicoEspecialidadService.obtenerEspecialidadDelMedico(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/medico-especialidad/medico/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicoId").value(1));
    }

    @Test
    @DisplayName("Obtener médicos por especialidad debe retornar 200")
    void obtenerMedicosPorEspecialidad() throws Exception {
        when(medicoEspecialidadService.obtenerMedicosPorEspecialidad(2L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/medico-especialidad/especialidad/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].especialidadId").value(2));
    }
}
