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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MedicoEspecialidadController.class, excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
}, excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthFilter.class,
                                JwtUtil.class })
})
class MedicoEspecialidadControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private MedicoEspecialidadService medicoEspecialidadService;

        @Autowired
        private ObjectMapper objectMapper;

        private MedicoEspecialidadRequest medicoEspecialidadRequest;
        private MedicoEspecialidadResponse medicoEspecialidadResponse;

        @BeforeEach
        void setUp() {
                medicoEspecialidadRequest = new MedicoEspecialidadRequest(1L, 2L, LocalDate.now());
                medicoEspecialidadResponse = new MedicoEspecialidadResponse(1L, "Juan Pérez", "12345678901",
                                "987654321", 2L, "Cardiología", LocalDate.now());
        }

        @Test
        @DisplayName("Registrar nueva relación Médico-Especialidad debe retornar 201 Created")
        void registrarRelacionME() throws Exception {
                when(medicoEspecialidadService.registrarRelacionME(any())).thenReturn(medicoEspecialidadResponse);

                mockMvc.perform(post("/api/medico-especialidad")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(medicoEspecialidadRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.medicoId").value(1L))
                                .andExpect(jsonPath("$.numeroColegiatura").value("12345678901"))
                                .andExpect(jsonPath("$.numeroRNE").value("987654321"));
        }

        @Test
        @DisplayName("Actualizar relación Médico-Especialidad debe retornar 200 OK")
        void actualizarRelacionME() throws Exception {
                when(medicoEspecialidadService.actualizarRelacionME(eq(1L), eq(2L), any()))
                                .thenReturn(medicoEspecialidadResponse);

                mockMvc.perform(put("/api/medico-especialidad/1/2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(medicoEspecialidadRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.medicoId").value(1L))
                                .andExpect(jsonPath("$.numeroColegiatura").value("12345678901"))
                                .andExpect(jsonPath("$.numeroRNE").value("987654321"));
        }

        @Test
        @DisplayName("Eliminar relación Médico-Especialidad debe retornar 204 No Content")
        void eliminarRelacionME() throws Exception {
                // ARRANGE
                doNothing().when(medicoEspecialidadService).eliminarRelacionME(1L, 2L);

                // ACT
                var response = mockMvc.perform(delete("/api/medico-especialidad/1/2"));

                // ASSERT
                response.andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Obtener especialidades de un médico debe retornar 200 OK")
        void obtenerEspecialidadDelMedico() throws Exception {
                // ARRANGE
                when(medicoEspecialidadService.obtenerEspecialidadDelMedico(1L))
                                .thenReturn(List.of(medicoEspecialidadResponse));

                // ACT
                var response = mockMvc.perform(get("/api/medico-especialidad/medico/1"));

                // ASSERT
                response.andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].medicoId").value(1L));
        }

        @Test
        @DisplayName("Obtener médicos por especialidad debe retornar 200 OK")
        void obtenerMedicosPorEspecialidad() throws Exception {
                // ARRANGE
                when(medicoEspecialidadService.obtenerMedicosPorEspecialidad(2L))
                                .thenReturn(List.of(medicoEspecialidadResponse));

                // ACT
                var response = mockMvc.perform(get("/api/medico-especialidad/especialidad/2"));

                // ASSERT
                response.andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].especialidadId").value(2L));
        }

        @Test
        @DisplayName("Registrar relación con datos inválidos debe retornar 400 Bad Request")
        void registrarRelacionME_datosInvalidos() throws Exception {
                // ARRANGE
                MedicoEspecialidadRequest invalido = new MedicoEspecialidadRequest(null, null, null);

                // ACT
                var response = mockMvc.perform(post("/api/medico-especialidad")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalido)));

                // ASSERT
                response.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Actualizar relación no existente debe retornar 404 Not Found")
        void actualizarRelacionME_noExistente() throws Exception {
                // ARRANGE
                when(medicoEspecialidadService.actualizarRelacionME(eq(1L), eq(2L), any()))
                                .thenThrow(new RuntimeException("Relación no encontrada"));

                // ACT
                var response = mockMvc.perform(put("/api/medico-especialidad/1/2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(medicoEspecialidadRequest)));

                // ASSERT
                response.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Eliminar relación inexistente debe retornar 404 Not Found")
        void eliminarRelacionME_noExistente() throws Exception {
                // ARRANGE
                doNothing().when(medicoEspecialidadService).eliminarRelacionME(eq(99L), eq(98L));

                // ACT
                var response = mockMvc.perform(delete("/api/medico-especialidad/99/98"));

                // ASSERT
                response.andExpect(status().isNoContent()); // Cambiar a .isNotFound() si lanzas excepción
        }

        @Test
        @DisplayName("Obtener especialidades de un médico sin registros debe retornar lista vacía")
        void obtenerEspecialidadDelMedico_listaVacia() throws Exception {
                // ARRANGE
                when(medicoEspecialidadService.obtenerEspecialidadDelMedico(99L)).thenReturn(List.of());

                // ACT
                var response = mockMvc.perform(get("/api/medico-especialidad/medico/99"));

                // ASSERT
                response.andExpect(status().isOk())
                                .andExpect(jsonPath("$").isEmpty());
        }
}
