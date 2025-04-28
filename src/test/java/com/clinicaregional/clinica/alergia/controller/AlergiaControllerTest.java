package com.clinicaregional.clinica.alergia.controller;

import com.clinicaregional.clinica.controller.AlergiaController;
import com.clinicaregional.clinica.dto.AlergiaDTO;
import com.clinicaregional.clinica.enums.TipoAlergia;
import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.AlergiaService;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AlergiaController.class, excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
}, excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthFilter.class,
                                JwtUtil.class })
})
class AlergiaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AlergiaService alergiaService;

        @Autowired
        private ObjectMapper objectMapper;

        private AlergiaDTO alergiaDTO;

        @BeforeEach
        void setUp() {
                alergiaDTO = new AlergiaDTO(1L, "Polvo", TipoAlergia.AMBIENTAL);
        }

        @Test
        @DisplayName("Listar todas las alergias debe retornar 200 OK")
        void listarAlergias() throws Exception {
                when(alergiaService.listarAlergias()).thenReturn(List.of(alergiaDTO));

                mockMvc.perform(get("/api/alergias"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].nombre").value("Polvo"));
        }

        @Test
        @DisplayName("Listar alergias por tipo debe retornar 200 OK")
        void listarAlergiasPorTipo() throws Exception {
                when(alergiaService.listarAlergiasPorTipo(TipoAlergia.AMBIENTAL)).thenReturn(List.of(alergiaDTO));

                mockMvc.perform(get("/api/alergias/AMBIENTAL"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].tipoAlergia").value("AMBIENTAL"));
        }

        @Test
        @DisplayName("Obtener alergia por ID existente debe retornar 200 OK")
        void obtenerAlergiaPorId_existente() throws Exception {
                when(alergiaService.getAlergiaPorId(1L)).thenReturn(Optional.of(alergiaDTO));

                mockMvc.perform(get("/api/alergias/id/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombre").value("Polvo"));
        }

        @Test
        @DisplayName("Crear nueva alergia debe retornar 201 Created")
        void crearAlergia() throws Exception {
                when(alergiaService.crearAlergia(any())).thenReturn(alergiaDTO);

                mockMvc.perform(post("/api/alergias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(alergiaDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.nombre").value("Polvo"));
        }

        @Test
        @DisplayName("Actualizar alergia existente debe retornar 200 OK")
        void actualizarAlergia() throws Exception {
                when(alergiaService.updateAlergia(any(), any())).thenReturn(alergiaDTO);

                mockMvc.perform(put("/api/alergias/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(alergiaDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombre").value("Polvo"));
        }

        @Test
        @DisplayName("Eliminar alergia existente debe retornar 204 No Content")
        void eliminarAlergia() throws Exception {
                mockMvc.perform(delete("/api/alergias/1"))
                                .andExpect(status().isNoContent());
        }
}
