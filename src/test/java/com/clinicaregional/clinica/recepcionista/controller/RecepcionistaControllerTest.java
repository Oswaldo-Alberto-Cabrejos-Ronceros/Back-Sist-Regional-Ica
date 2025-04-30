package com.clinicaregional.clinica.recepcionista.controller;

import com.clinicaregional.clinica.controller.RecepcionistaController;
import com.clinicaregional.clinica.dto.request.RecepcionistaRequest;
import com.clinicaregional.clinica.dto.response.RecepcionistaResponse;
import com.clinicaregional.clinica.enums.TurnoTrabajo;
import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.RecepcionistaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RecepcionistaController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {JwtAuthFilter.class, JwtUtil.class})
})
class RecepcionistaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecepcionistaService recepcionistaService;

    @Autowired
    private ObjectMapper objectMapper;

    private RecepcionistaRequest recepcionistaRequest;
    private RecepcionistaResponse recepcionistaResponse;

    @BeforeEach
    void setUp() {
        recepcionistaRequest = new RecepcionistaRequest();
        recepcionistaRequest.setNombres("Lucia");
        recepcionistaRequest.setApellidos("Gomez");
        recepcionistaRequest.setNumeroDocumento("12345678");
        recepcionistaRequest.setTipoDocumentoId(1L);
        recepcionistaRequest.setTelefono("987654321");
        recepcionistaRequest.setDireccion("Av. Principal 123");
        recepcionistaRequest.setTurnoTrabajo(TurnoTrabajo.DIURNO);
        recepcionistaRequest.setFechaContratacion(LocalDate.now());
        recepcionistaRequest.setUsuarioId(1L);
        recepcionistaRequest.setCorreo("lucia@clinica.com");
        recepcionistaRequest.setPassword("Lucia123");

        recepcionistaResponse = RecepcionistaResponse.builder()
                .id(1L)
                .nombres("Lucia")
                .apellidos("Gomez")
                .numeroDocumento("12345678")
                .tipoDocumentoId(1L)
                .telefono("987654321")
                .direccion("Av. Principal 123")
                .turnoTrabajo(TurnoTrabajo.DIURNO)
                .fechaContratacion(LocalDate.now())
                .usuarioId(1L)
                .build();
    }

    @Test
    @DisplayName("Listar recepcionistas debe retornar 200 OK")
    void listarRecepcionistas() throws Exception {
        when(recepcionistaService.listar()).thenReturn(List.of(recepcionistaResponse));
        mockMvc.perform(get("/api/recepcionistas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombres").value("Lucia"));
    }

    @Test
    @DisplayName("Obtener recepcionista por ID debe retornar 200 OK")
    void obtenerRecepcionistaPorId() throws Exception {
        when(recepcionistaService.obtenerPorId(1L)).thenReturn(Optional.of(recepcionistaResponse));
        mockMvc.perform(get("/api/recepcionistas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apellidos").value("Gomez"));
    }

    @Test
    @DisplayName("Registrar recepcionista debe retornar 201 Created")
    void registrarRecepcionista() throws Exception {
        when(recepcionistaService.guardar(any())).thenReturn(recepcionistaResponse);
        mockMvc.perform(post("/api/recepcionistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recepcionistaRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroDocumento").value("12345678"));
    }

    @Test
    @DisplayName("Actualizar recepcionista debe retornar 200 OK")
    void actualizarRecepcionista() throws Exception {
        when(recepcionistaService.actualizar(any(), any())).thenReturn(recepcionistaResponse);
        mockMvc.perform(put("/api/recepcionistas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recepcionistaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.direccion").value("Av. Principal 123"));
    }

    @Test
    @DisplayName("Eliminar recepcionista debe retornar 204 No Content")
    void eliminarRecepcionista() throws Exception {
        mockMvc.perform(delete("/api/recepcionistas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Obtener recepcionista por ID inexistente debe retornar 404")
    void obtenerRecepcionistaPorId_inexistente() throws Exception {
        when(recepcionistaService.obtenerPorId(99L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/recepcionistas/99"))
                .andExpect(status().isNotFound());  
    }

    @Test
    @DisplayName("Registrar recepcionista con datos inválidos debe retornar 400")
    void registrarRecepcionista_datosInvalidos() throws Exception {
        RecepcionistaRequest invalido = new RecepcionistaRequest();
        invalido.setCorreo("correo-invalido");
        mockMvc.perform(post("/api/recepcionistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }
} 
