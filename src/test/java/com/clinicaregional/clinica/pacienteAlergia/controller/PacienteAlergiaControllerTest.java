package com.clinicaregional.clinica.pacienteAlergia.controller;

import com.clinicaregional.clinica.controller.PacienteAlergiaController;
import com.clinicaregional.clinica.dto.AlergiaDTO;
import com.clinicaregional.clinica.dto.PacienteAlergiaDTO;
import com.clinicaregional.clinica.enums.Gravedad;
import com.clinicaregional.clinica.enums.TipoAlergia;
import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.PacienteAlergiaService;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PacienteAlergiaController.class, excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
}, excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthFilter.class,
                                JwtUtil.class })
})
class PacienteAlergiaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private PacienteAlergiaService pacienteAlergiaService;

        @Autowired
        private ObjectMapper objectMapper;

        private PacienteAlergiaDTO pacienteAlergiaDTO;

        @BeforeEach
        void setUp() {
                pacienteAlergiaDTO = new PacienteAlergiaDTO();
                pacienteAlergiaDTO.setId(1L);
                pacienteAlergiaDTO.setPacienteId(1L);
                pacienteAlergiaDTO.setAlergia(new AlergiaDTO(1L, "Polen", TipoAlergia.AMBIENTAL));
                pacienteAlergiaDTO.setGravedad(Gravedad.MODERADA);
        }

        @Test
        @DisplayName("Listar todas las relaciones Paciente-Alergia debe retornar 200 OK")
        void listarPacienteAlergias() throws Exception {
                // ARRANGE
                when(pacienteAlergiaService.listarPacienteAlergias()).thenReturn(List.of(pacienteAlergiaDTO));

                // ACT
                var response = mockMvc.perform(get("/api/paciente-alergia"));

                // ASSERT
                response.andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1L));
        }

        @Test
        @DisplayName("Listar relaciones Paciente-Alergia por ID de paciente debe retornar 200 OK")
        void listarPacienteAlergiasPorPaciente() throws Exception {
                // ARRANGE
                when(pacienteAlergiaService.listarPacienteAlergiasPorPaciente(1L))
                                .thenReturn(List.of(pacienteAlergiaDTO));

                // ACT
                var response = mockMvc.perform(get("/api/paciente-alergia/paciente/1"));

                // ASSERT
                response.andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1L));
        }

        @Test
        @DisplayName("Obtener relación Paciente-Alergia por ID debe retornar 200 OK")
        void getPacienteAlergiaById() throws Exception {
                // ARRANGE
                when(pacienteAlergiaService.getPacienteAlergiaById(1L)).thenReturn(Optional.of(pacienteAlergiaDTO));

                // ACT
                var response = mockMvc.perform(get("/api/paciente-alergia/1"));

                // ASSERT
                response.andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L));
        }

        @Test
        @DisplayName("Crear nueva relación Paciente-Alergia debe retornar 201 Created")
        void createPacienteAlergia() throws Exception {
                // ARRANGE
                when(pacienteAlergiaService.createPacienteAlergia(any())).thenReturn(pacienteAlergiaDTO);

                // ACT
                var response = mockMvc.perform(post("/api/paciente-alergia")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pacienteAlergiaDTO)));

                // ASSERT
                response.andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1L));
        }

        @Test
        @DisplayName("Actualizar relación Paciente-Alergia debe retornar 200 OK")
        void updatePacienteAlergia() throws Exception {
                // ARRANGE
                when(pacienteAlergiaService.updatePacienteAlergia(eq(1L), any())).thenReturn(pacienteAlergiaDTO);

                // ACT
                var response = mockMvc.perform(put("/api/paciente-alergia/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pacienteAlergiaDTO)));

                // ASSERT
                response.andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L));
        }

        @Test
        @DisplayName("Eliminar relación Paciente-Alergia debe retornar 204 No Content")
        void deletePacienteAlergia() throws Exception {
                // ARRANGE
                doNothing().when(pacienteAlergiaService).deletePacienteAlergia(1L);

                // ACT
                var response = mockMvc.perform(delete("/api/paciente-alergia/1"));

                // ASSERT
                response.andExpect(status().isNoContent());
        }
}
