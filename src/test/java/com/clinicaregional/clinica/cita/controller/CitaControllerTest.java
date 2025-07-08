package com.clinicaregional.clinica.cita.controller;

import com.clinicaregional.clinica.controller.CitaController;
import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
import com.clinicaregional.clinica.dto.response.PacienteResponseDTO;
import com.clinicaregional.clinica.enums.EstadoCita;
import com.clinicaregional.clinica.enums.Sexo;
import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.CitaService;
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
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CitaController.class, excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
}, excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthFilter.class,
                                JwtUtil.class })
})
class CitaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private CitaService citaService;

        @Autowired
        private ObjectMapper objectMapper;

        private CitaRequest request;
        private CitaResponse response;

        @BeforeEach
        void setUp() {
                request = new CitaRequest(
                                LocalDate.of(2025, 6, 22),
                                LocalTime.of(10, 0),
                                "Notas",
                                "Antecedentes",
                                1L, 1L, 1L,
                                1L, 1L);

                response = new CitaResponse();
                response.setCitaId(100L);
                response.setFecha(request.getFecha());
                response.setHora(request.getHora());
                response.setEstadoCita(EstadoCita.PENDIENTE);
        }

        // @Test
        // @DisplayName("Registrar cita debe retornar 201 Created")
        // void registrarCita_debeRetornarCreado() throws Exception {
        //         when(citaService.registrar(any())).thenReturn(response);

        //         mockMvc.perform(post("/api/citas")
        //                         .contentType(MediaType.APPLICATION_JSON)
        //                         .content(objectMapper.writeValueAsString(request)))
        //                         .andExpect(status().isCreated())
        //                         .andExpect(jsonPath("$.citaId").value(100))
        //                         .andExpect(jsonPath("$.estadoCita").value("PENDIENTE"));
        // }

        @Test
        @DisplayName("Obtener cita por ID debe retornar 200 OK")
        void obtenerCitaPorId_debeRetornarOk() throws Exception {
                when(citaService.obtenerPorId(100L)).thenReturn(response);

                mockMvc.perform(get("/api/citas/100"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.citaId").value(100));
        }

        @Test
        @DisplayName("Listar todas las citas debe retornar 200 OK")
        void listarCitas_debeRetornarLista() throws Exception {
                when(citaService.listarTodas()).thenReturn(List.of(response));

                mockMvc.perform(get("/api/citas"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].citaId").value(100));
        }

        // @Test
        // @DisplayName("Actualizar cita debe retornar 200 OK")
        // void actualizarCita_debeRetornarOk() throws Exception {
        //         when(citaService.actualizar(eq(100L), any())).thenReturn(response);

        //         mockMvc.perform(put("/api/citas/100")
        //                         .contentType(MediaType.APPLICATION_JSON)
        //                         .content(objectMapper.writeValueAsString(request)))
        //                         .andExpect(status().isOk())
        //                         .andExpect(jsonPath("$.citaId").value(100));
        // }

        @Test
        @DisplayName("Eliminar cita debe retornar 204 No Content")
        void eliminarCita_debeRetornarNoContent() throws Exception {
                mockMvc.perform(delete("/api/citas/100"))
                                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Confirmar cita debe retornar 200 OK")
        void confirmarCita_debeRetornarOk() throws Exception {
                response.setEstadoCita(EstadoCita.CONFIRMADA);
                when(citaService.confirmarCita(100L)).thenReturn(response);

                mockMvc.perform(put("/api/citas/confirmar/100"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.estadoCita").value("CONFIRMADA"));
        }

        @Test
        @DisplayName("Cancelar cita debe retornar 200 OK")
        void cancelarCita_debeRetornarOk() throws Exception {
                response.setEstadoCita(EstadoCita.CANCELADA);
                when(citaService.cancelarCita(100L)).thenReturn(response);

                mockMvc.perform(put("/api/citas/cancelar/100"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.estadoCita").value("CANCELADA"));
        }

        @Test
        @DisplayName("Atender cita debe retornar 200 OK")
        void atenderCita_debeRetornarOk() throws Exception {
                response.setEstadoCita(EstadoCita.ATENDIDA);
                when(citaService.atenderCita(100L)).thenReturn(response);

                mockMvc.perform(put("/api/citas/atender/100"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.estadoCita").value("ATENDIDA"));
        }

        // @Test
        // @DisplayName("Reprogramar cita debe retornar 200 OK")
        // void reprogramarCita_debeRetornarOk() throws Exception {
        //         response.setEstadoCita(EstadoCita.REPROGRAMADA);
        //         when(citaService.reprogramarCita(eq(100L), any())).thenReturn(response);

        //         mockMvc.perform(put("/api/citas/reprogramar/100")
        //                         .contentType(MediaType.APPLICATION_JSON)
        //                         .content(objectMapper.writeValueAsString(request)))
        //                         .andExpect(status().isOk())
        //                         .andExpect(jsonPath("$.estadoCita").value("REPROGRAMADA"));
        // }

        @Test
        @DisplayName("Obtener pacientes por médico debe retornar 200 OK")
        void obtenerPacientesPorMedico_debeRetornarOk() throws Exception {
                PacienteResponseDTO paciente = new PacienteResponseDTO();
                paciente.setNombres("Juan");
                paciente.setApellidos("Pérez");
                paciente.setNumeroIdentificacion("12345678");
                paciente.setTelefono("987654321");
                paciente.setSexo(Sexo.MASCULINO);
                paciente.setEdad(30);
                paciente.setAntecedentes("Diabetes");

                when(citaService.obtenerPacientesPorMedicoConCitasConfirmadasOAtendidas(1L))
                                .thenReturn(List.of(paciente));

                mockMvc.perform(get("/api/citas/medico/1/pacientes"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].nombres").value("Juan"))
                                .andExpect(jsonPath("$[0].apellidos").value("Pérez"));
        }
}
