package com.clinicaregional.clinica.paciente.controller;

import com.clinicaregional.clinica.controller.PacienteController;
import com.clinicaregional.clinica.dto.PacienteDTO;
import com.clinicaregional.clinica.dto.TipoDocumentoDTO;
import com.clinicaregional.clinica.enums.Sexo;
import com.clinicaregional.clinica.enums.TipoSangre;
import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.PacienteService;
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

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PacienteController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthFilter.class, JwtUtil.class })
})
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PacienteService pacienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private PacienteDTO pacienteDTO;

    @BeforeEach
    void setUp() {
        pacienteDTO = new PacienteDTO();
        pacienteDTO.setId(1L);
        pacienteDTO.setNombres("Juan");
        pacienteDTO.setApellidos("Pérez");
        pacienteDTO.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        pacienteDTO.setSexo(Sexo.MASCULINO);
        pacienteDTO.setNumeroIdentificacion("12345678");
        pacienteDTO.setNacionalidad("PERUANA");
        pacienteDTO.setTelefono("987654321");
        pacienteDTO.setDireccion("Calle Falsa 123");
        pacienteDTO.setTipoSangre(TipoSangre.O_POSITIVO);
        pacienteDTO.setTipoDocumento(new TipoDocumentoDTO(1L, "DNI", "Documento Nacional de Identidad"));
    }

    @Test
    @DisplayName("Listar todos los pacientes debe retornar 200 OK")
    void listarPacientes() throws Exception {
        when(pacienteService.listarPacientes()).thenReturn(List.of(pacienteDTO));

        mockMvc.perform(get("/api/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombres").value("Juan"));
    }

    @Test
    @DisplayName("Obtener paciente por ID debe retornar 200 OK")
    void obtenerPacientePorId() throws Exception {
        when(pacienteService.getPacientePorId(1L)).thenReturn(Optional.of(pacienteDTO));

        mockMvc.perform(get("/api/pacientes/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombres").value("Juan"));
    }

    @Test
    @DisplayName("Obtener paciente por identificación debe retornar 200 OK")
    void obtenerPacientePorIdentificacion() throws Exception {
        when(pacienteService.getPacientePorIdentificacion("12345678")).thenReturn(Optional.of(pacienteDTO));

        mockMvc.perform(get("/api/pacientes/num-identificacion/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroIdentificacion").value("12345678"));
    }

    @Test
    @DisplayName("Crear nuevo paciente debe retornar 201 Created")
    void crearPaciente() throws Exception {
        when(pacienteService.crearPaciente(any())).thenReturn(pacienteDTO);

        mockMvc.perform(post("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pacienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombres").value("Juan"));
    }

    @Test
    @DisplayName("Actualizar paciente debe retornar 200 OK")
    void actualizarPaciente() throws Exception {
        when(pacienteService.actualizarPaciente(any(), any())).thenReturn(pacienteDTO);

        mockMvc.perform(put("/api/pacientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pacienteDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apellidos").value("Pérez"));
    }

    @Test
    @DisplayName("Eliminar paciente debe retornar 204 No Content")
    void eliminarPaciente() throws Exception {
        mockMvc.perform(delete("/api/pacientes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Obtener paciente por ID inexistente debe retornar 404 Not Found")
    void obtenerPacientePorId_inexistente() throws Exception {
        // Arrange
        when(pacienteService.getPacientePorId(99L)).thenReturn(Optional.empty());

        // Act + Assert
        mockMvc.perform(get("/api/pacientes/id/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Obtener paciente por identificación inexistente debe retornar 404 Not Found")
    void obtenerPacientePorIdentificacion_inexistente() throws Exception {
        // Arrange
        when(pacienteService.getPacientePorIdentificacion("00000000")).thenReturn(Optional.empty());

        // Act + Assert
        mockMvc.perform(get("/api/pacientes/num-identificacion/00000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Crear paciente con datos inválidos debe retornar 400 Bad Request")
    void crearPaciente_datosInvalidos() throws Exception {
        // Arrange: paciente sin nombre (violación de validación)
        pacienteDTO.setNombres(null);

        // Act + Assert
        mockMvc.perform(post("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pacienteDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Actualizar paciente inexistente debe retornar 404 Not Found")
    void actualizarPaciente_inexistente() throws Exception {
        // Arrange
        when(pacienteService.actualizarPaciente(any(), any()))
                .thenThrow(new RuntimeException("Paciente no encontrado"));

        // Act + Assert
        mockMvc.perform(put("/api/pacientes/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pacienteDTO)))
                .andExpect(status().isNotFound());
    }
}
