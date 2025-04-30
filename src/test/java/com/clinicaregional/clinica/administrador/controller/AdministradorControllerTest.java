package com.clinicaregional.clinica.administrador.controller;

import com.clinicaregional.clinica.controller.AdministradorController;
import com.clinicaregional.clinica.dto.AdministradorDTO;
import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.RegisterAdministradorRequest;
import com.clinicaregional.clinica.dto.request.UsuarioRequestDTO;
import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.AdministradorService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdministradorController.class, excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
}, excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthFilter.class,
                                JwtUtil.class })
})
class AdministradorControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AdministradorService administradorService;

        @Autowired
        private ObjectMapper objectMapper;
        private AdministradorDTO administradorDTO;
        private UsuarioRequestDTO usuarioRequestDTO;
        private RegisterAdministradorRequest registerRequest;

        @BeforeEach
        void setUp() {
                administradorDTO = new AdministradorDTO(
                                1L, "Diego", "Aguilar", "12345678", 1L, "999999999",
                                "Av. Siempre Viva", LocalDate.of(2020, 1, 1), 1L);

                usuarioRequestDTO = new UsuarioRequestDTO(
                                "admin@clinica.pe", "Admin123", true, new RolDTO(2L, "ADMIN"));

                registerRequest = new RegisterAdministradorRequest();
                registerRequest.setAdministrador(administradorDTO);
                registerRequest.setUsuario(usuarioRequestDTO);
        }

        @Test
        @DisplayName("GET /api/administradores debe retornar lista")
        void listarAdministradores() throws Exception {
                when(administradorService.listarAdministradores()).thenReturn(List.of(administradorDTO));

                mockMvc.perform(get("/api/administradores"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].nombres").value("Diego"));
        }

        @Test
        @DisplayName("GET /api/administradores/{id} debe retornar un administrador")
        void obtenerAdministradorPorId() throws Exception {
                when(administradorService.getAdministradorById(1L)).thenReturn(Optional.of(administradorDTO));

                mockMvc.perform(get("/api/administradores/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.apellidos").value("Aguilar"));
        }

        @Test
        @DisplayName("POST /api/administradores/register debe crear un administrador")
        void crearAdministrador() throws Exception {
                when(administradorService.createAdministrador(any())).thenReturn(administradorDTO);

                mockMvc.perform(post("/api/administradores")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.numeroDocumento").value("12345678"));

        }

        @Test
        @DisplayName("DELETE /api/administradores/{id} debe retornar 204")
        void eliminarAdministrador() throws Exception {
                mockMvc.perform(delete("/api/administradores/1"))
                                .andExpect(status().isNoContent());
        }
}
