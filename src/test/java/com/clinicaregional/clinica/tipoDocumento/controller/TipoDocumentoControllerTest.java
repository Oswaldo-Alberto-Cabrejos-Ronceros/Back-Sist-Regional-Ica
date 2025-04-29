package com.clinicaregional.clinica.tipoDocumento.controller;

import com.clinicaregional.clinica.controller.TipoDocumentoController;
import com.clinicaregional.clinica.dto.TipoDocumentoDTO;
import com.clinicaregional.clinica.service.TipoDocumentoService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TipoDocumentoController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { com.clinicaregional.clinica.security.JwtAuthFilter.class, com.clinicaregional.clinica.security.JwtUtil.class })
        })
class TipoDocumentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TipoDocumentoService tipoDocumentoService;

    @Autowired
    private ObjectMapper objectMapper;

    private TipoDocumentoDTO tipoDocumentoDTO;

    @BeforeEach
    void setUp() {
        tipoDocumentoDTO = TipoDocumentoDTO.builder()
                .id(1L)
                .nombre("DNI")
                .descripcion("Documento Nacional de Identidad")
                .build();
    }

    @Test
    void listarTiposDocumento_exitoso() throws Exception {
        // Arrange
        when(tipoDocumentoService.listarTipoDocumento()).thenReturn(List.of(tipoDocumentoDTO));

        // Act
        var response = mockMvc.perform(get("/api/tipos-documentos"));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombre").value("DNI"));
    }

    @Test
    void obtenerTipoDocumentoPorId_existente() throws Exception {
        // Arrange
        when(tipoDocumentoService.getTipoDocumentoById(1L)).thenReturn(Optional.of(tipoDocumentoDTO));

        // Act
        var response = mockMvc.perform(get("/api/tipos-documentos/1"));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre").value("DNI"));
    }

    @Test
    void obtenerTipoDocumentoPorId_noExistente() throws Exception {
        // Arrange
        when(tipoDocumentoService.getTipoDocumentoById(99L)).thenReturn(Optional.empty());

        // Act
        var response = mockMvc.perform(get("/api/tipos-documentos/99"));

        // Assert
        response.andExpect(status().isNotFound());
    }

    @Test
    void crearTipoDocumento_exitoso() throws Exception {
        // Arrange
        when(tipoDocumentoService.createTipoDocumento(any(TipoDocumentoDTO.class))).thenReturn(tipoDocumentoDTO);

        // Act
        var response = mockMvc.perform(post("/api/tipos-documentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tipoDocumentoDTO)));

        // Assert
        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre").value("DNI"));
    }

    @Test
    void actualizarTipoDocumento_exitoso() throws Exception {
        // Arrange
        when(tipoDocumentoService.updateTipoDocumento(eq(1L), any(TipoDocumentoDTO.class))).thenReturn(tipoDocumentoDTO);

        // Act
        var response = mockMvc.perform(put("/api/tipos-documentos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tipoDocumentoDTO)));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre").value("DNI"));
    }

    @Test
    void eliminarTipoDocumento_exitoso() throws Exception {
        // Arrange
        doNothing().when(tipoDocumentoService).deleteTipoDocumento(1L);

        // Act
        var response = mockMvc.perform(delete("/api/tipos-documentos/1"));

        // Assert
        response.andExpect(status().isNoContent());
    }
}
