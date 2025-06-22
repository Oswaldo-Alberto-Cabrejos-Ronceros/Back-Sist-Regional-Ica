package com.clinicaregional.clinica.horario_bloque.controller;

import com.clinicaregional.clinica.controller.HorarioBloqueController;
import com.clinicaregional.clinica.dto.request.HorarioBloqueRequest;
import com.clinicaregional.clinica.dto.response.HorarioBloqueResponse;
import com.clinicaregional.clinica.enums.EstadoBloque;
import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.HorarioBloqueService;
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

@WebMvcTest(controllers = HorarioBloqueController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthFilter.class, JwtUtil.class })
})
class HorarioBloqueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HorarioBloqueService horarioBloqueService;

    @Autowired
    private ObjectMapper objectMapper;

    private HorarioBloqueRequest request;
    private HorarioBloqueResponse response;

    @BeforeEach
    void setUp() {
        request = HorarioBloqueRequest.builder()
                .fecha(LocalDate.now())
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(9, 30))
                .estadoBloque(EstadoBloque.DISPONIBLE)
                .disponibilidadId(1L)
                .build();

        response = new HorarioBloqueResponse(
                1L,
                request.getFecha(),
                request.getHoraInicio(),
                request.getHoraFin(),
                request.getEstadoBloque().name(),
                1L,
                null);
    }

    @Test
    @DisplayName("Registrar horario bloque debe retornar 200 OK")
    void registrarHorarioBloque_debeRetornarOk() throws Exception {
        when(horarioBloqueService.registrar(any())).thenReturn(response);

        mockMvc.perform(post("/api/horario-bloques")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estadoBloque").value("DISPONIBLE"))
                .andExpect(jsonPath("$.disponibilidadId").value(1));
    }

    @Test
    @DisplayName("Obtener horario bloque por ID debe retornar 200 OK")
    void obtenerHorarioBloquePorId_debeRetornarOk() throws Exception {
        when(horarioBloqueService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/horario-bloques/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estadoBloque").value("DISPONIBLE"));
    }

    @Test
    @DisplayName("Listar horario bloques por disponibilidad debe retornar 200 OK")
    void listarPorDisponibilidad_debeRetornarOk() throws Exception {
        when(horarioBloqueService.listarPorDisponibilidad(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/horario-bloques/disponibilidad/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("Listar horario bloques por médico debe retornar 200 OK")
    void listarPorMedico_debeRetornarOk() throws Exception {
        when(horarioBloqueService.listarPorMedico(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/horario-bloques/medico/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("Listar horario bloques por fecha debe retornar 200 OK")
    void listarPorFecha_debeRetornarOk() throws Exception {
        when(horarioBloqueService.listarPorFecha(LocalDate.now())).thenReturn(List.of(response));

        mockMvc.perform(get("/api/horario-bloques/fecha")
                .param("fecha", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("Listar horario bloques por especialidad debe retornar 200 OK")
    void listarPorEspecialidad_debeRetornarOk() throws Exception {
        when(horarioBloqueService.listarPorEspecialidad(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/horario-bloques/especialidad/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("Actualizar estado de horario bloque debe retornar 200 OK")
    void actualizarEstado_debeRetornarOk() throws Exception {
        when(horarioBloqueService.actualizarEstado(eq(1L), eq("OCUPADO"))).thenReturn(response);

        mockMvc.perform(put("/api/horario-bloques/1/estado")
                .param("estado", "OCUPADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
