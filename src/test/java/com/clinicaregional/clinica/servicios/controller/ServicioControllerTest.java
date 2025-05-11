package com.clinicaregional.clinica.servicios.controller;

import com.clinicaregional.clinica.controller.ServicioController;
import com.clinicaregional.clinica.dto.request.ServicioRequest;
import com.clinicaregional.clinica.dto.response.ServicioResponse;
import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.ServicioService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ServicioController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthFilter.class, JwtUtil.class })
})
class ServicioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServicioService servicioService;

    @Autowired
    private ObjectMapper objectMapper;

    private ServicioRequest servicioRequest;
    private ServicioResponse servicioResponse;

    @BeforeEach
    void setUp() {
        servicioRequest = new ServicioRequest("Pediatría", "Atiende a niños", "pediatria.jpg");
        servicioResponse = new ServicioResponse(1L, "Pediatría", "Atiende a niños", "pediatria.jpg");
    }

    @Test
    @DisplayName("Crear servicio debe retornar 200 OK")
    void crearServicio() throws Exception {
        when(servicioService.agregarServicio(any())).thenReturn(servicioResponse);

        mockMvc.perform(post("/api/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servicioRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Pediatría"));
    }

    @Test
    @DisplayName("Actualizar servicio existente debe retornar 200 OK")
    void actualizarServicio() throws Exception {
        when(servicioService.actualizarServicio(eq(1L), any())).thenReturn(servicioResponse);

        mockMvc.perform(put("/api/servicios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servicioRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value("Atiende a niños"));
    }

    @Test
    @DisplayName("Eliminar servicio debe retornar 204 No Content")
    void eliminarServicio() throws Exception {
        doNothing().when(servicioService).eliminarServicio(1L);

        mockMvc.perform(delete("/api/servicios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Eliminar servicio inexistente debe retornar 404 Not Found")
    void eliminarServicioInexistente() throws Exception {
        doThrow(new RuntimeException("Servicio no encontrado")).when(servicioService).eliminarServicio(999L);

        mockMvc.perform(delete("/api/servicios/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Actualizar servicio con error debe retornar 400 Bad Request")
    void actualizarServicioConError() throws Exception {
        when(servicioService.actualizarServicio(eq(1L), any()))
                .thenThrow(new RuntimeException("Ya existe un servicio con el nombre ingresado"));

        mockMvc.perform(put("/api/servicios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servicioRequest)))
                .andExpect(status().isBadRequest());
    }
}
