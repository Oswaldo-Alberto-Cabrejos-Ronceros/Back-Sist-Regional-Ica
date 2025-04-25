package com.clinicaregional.clinica.usuarios.controller;

import com.clinicaregional.clinica.controller.UsuarioController;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.UsuarioRequestDTO;
import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = UsuarioController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    },
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthFilter.class, JwtUtil.class })
    }
)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioDTO usuarioDTO;
    private UsuarioRequestDTO usuarioRequestDTO;

    @BeforeEach
    void setUp() {
        RolDTO rolDTO = new RolDTO(1L, "PACIENTE", "Paciente del sistema");
        usuarioDTO = new UsuarioDTO(1L, "tester5461@gmail.com", true, rolDTO);
        usuarioRequestDTO = new UsuarioRequestDTO("tester5461@gmail.com", "Tester5461", true, rolDTO);
    }

    @Test
    void listarUsuarios_debeRetornarListaUsuarios() throws Exception {
        when(usuarioService.listarUsuarios()).thenReturn(List.of(usuarioDTO));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].correo").value("tester5461@gmail.com"));
    }

    @Test
    void obtenerUsuarioPorId_existente_debeRetornarUsuario() throws Exception {
        when(usuarioService.obtenerPorId(1L)).thenReturn(Optional.of(usuarioDTO));

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("tester5461@gmail.com"));
    }

    @Test
    void obtenerUsuarioPorId_noExistente_debeRetornar404() throws Exception {
        when(usuarioService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearUsuario_exitoso() throws Exception {
        when(usuarioService.guardar(any(UsuarioRequestDTO.class))).thenReturn(usuarioDTO);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.correo").value("tester5461@gmail.com"));
    }

    @Test
    void actualizarUsuario_exitoso() throws Exception {
        when(usuarioService.actualizar(eq(1L), any(UsuarioRequestDTO.class))).thenReturn(usuarioDTO);

        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("tester5461@gmail.com"));
    }

    @Test
    void eliminarUsuario_exitoso() throws Exception {
        doNothing().when(usuarioService).eliminar(1L);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());
    }
}
