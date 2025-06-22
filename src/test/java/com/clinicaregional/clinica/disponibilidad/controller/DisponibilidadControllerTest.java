package com.clinicaregional.clinica.disponibilidad.controller;

import com.clinicaregional.clinica.controller.DisponibilidadController;
import com.clinicaregional.clinica.dto.request.DisponibilidadRequest;
import com.clinicaregional.clinica.dto.response.DisponibilidadResponse;
import com.clinicaregional.clinica.enums.DiaSemana;
import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.DisponibilidadService;
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

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DisponibilidadController.class, excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
}, excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthFilter.class,
                                JwtUtil.class })
})
class DisponibilidadControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private DisponibilidadService disponibilidadService;

        @Autowired
        private ObjectMapper objectMapper;

        private DisponibilidadRequest request;
        private DisponibilidadResponse response;

        @BeforeEach
        void setUp() {
                request = new DisponibilidadRequest(
                                DiaSemana.MARTES,
                                LocalTime.of(9, 0),
                                LocalTime.of(12, 0),
                                "Turno mañana",
                                1L,
                                45);

                response = new DisponibilidadResponse(
                                10L,
                                DiaSemana.MARTES,
                                LocalTime.of(9, 0),
                                LocalTime.of(12, 0),
                                "Turno mañana",
                                1L);
        }

        @Test
        @DisplayName("Registrar disponibilidad debe retornar 201 Created")
        void registrarDisponibilidad_debeRetornarCreado() throws Exception {
                when(disponibilidadService.registrar(any())).thenReturn(response);

                mockMvc.perform(post("/api/disponibilidad")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(10))
                                .andExpect(jsonPath("$.diaSemana").value("MARTES"))
                                .andExpect(jsonPath("$.notas").value("Turno mañana"))
                                .andExpect(jsonPath("$.medicoId").value(1));
        }

        @Test
        @DisplayName("Obtener disponibilidad por ID debe retornar 200 OK")
        void obtenerDisponibilidadPorId_debeRetornarOk() throws Exception {
                when(disponibilidadService.obtenerPorId(10L)).thenReturn(response);

                mockMvc.perform(get("/api/disponibilidad/10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(10))
                                .andExpect(jsonPath("$.diaSemana").value("MARTES"))
                                .andExpect(jsonPath("$.notas").value("Turno mañana"));
        }

        @Test
        @DisplayName("Listar todas las disponibilidades debe retornar 200 OK")
        void listarDisponibilidades_debeRetornarLista() throws Exception {
                when(disponibilidadService.listar()).thenReturn(List.of(response));

                mockMvc.perform(get("/api/disponibilidad"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(10));
        }

        @Test
        @DisplayName("Listar disponibilidades por ID de médico debe retornar 200 OK")
        void listarDisponibilidadesPorMedico_debeRetornarLista() throws Exception {
                when(disponibilidadService.listarPorMedicoId(1L)).thenReturn(List.of(response));

                mockMvc.perform(get("/api/disponibilidad/medico/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].medicoId").value(1));
        }

        @Test
        @DisplayName("Actualizar disponibilidad debe retornar 200 OK")
        void actualizarDisponibilidad_debeRetornarActualizado() throws Exception {
                DisponibilidadRequest updatedRequest = new DisponibilidadRequest(
                                DiaSemana.MIERCOLES,
                                LocalTime.of(14, 0),
                                LocalTime.of(16, 0),
                                "Turno tarde",
                                1L,
                                30);

                DisponibilidadResponse updatedResponse = new DisponibilidadResponse(
                                10L,
                                DiaSemana.MIERCOLES,
                                LocalTime.of(14, 0),
                                LocalTime.of(16, 0),
                                "Turno tarde",
                                1L);

                when(disponibilidadService.actualizar(eq(10L), any(DisponibilidadRequest.class)))
                                .thenReturn(updatedResponse);

                mockMvc.perform(put("/api/disponibilidad/10")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.diaSemana").value("MIERCOLES"))
                                .andExpect(jsonPath("$.notas").value("Turno tarde"));
        }

        @Test
        @DisplayName("Eliminar disponibilidad debe retornar 204 No Content")
        void eliminarDisponibilidad_debeRetornarNoContent() throws Exception {
                mockMvc.perform(delete("/api/disponibilidad/10"))
                                .andExpect(status().isNoContent());
        }
}
