package com.clinicaregional.clinica.medico.controller;

import com.clinicaregional.clinica.controller.MedicoController;
import com.clinicaregional.clinica.dto.request.MedicoRequestDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponseDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponsePublicDTO;
import com.clinicaregional.clinica.enums.TipoContrato;
import com.clinicaregional.clinica.enums.TipoMedico;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.MedicoService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MedicoController.class, excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
}, excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthFilter.class,
                                JwtUtil.class })
})
class MedicoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private MedicoService medicoService;

        @Autowired
        private ObjectMapper objectMapper;

        private MedicoResponseDTO responseDTO;
        private MedicoRequestDTO requestDTO;
        private MedicoResponsePublicDTO publicDTO;

        @BeforeEach
        void setUp() {
                responseDTO = new MedicoResponseDTO();
                responseDTO.setId(1L);
                responseDTO.setNombres("Ana");

                publicDTO = new MedicoResponsePublicDTO();
                publicDTO.setId(1L);
                publicDTO.setNombres("Ana");
                publicDTO.setDescripcion("Cardióloga");

                requestDTO = new MedicoRequestDTO();
                requestDTO.setNombres("Ana");
                requestDTO.setApellidos("Lopez");
                requestDTO.setNumeroColegiatura("12345678901");
                requestDTO.setNumeroRNE("123456789");
                requestDTO.setTipoDocumentoId(1L);
                requestDTO.setNumeroDocumento("78945612");
                requestDTO.setTelefono("999999999");
                requestDTO.setDireccion("Calle 123");
                requestDTO.setDescripcion("Cardióloga");
                requestDTO.setImagen("img.png");
                requestDTO.setFechaContratacion(LocalDateTime.now());
                requestDTO.setTipoContrato(TipoContrato.FIJO);
                requestDTO.setTipoMedico(TipoMedico.ESPECIALISTA);
                requestDTO.setCorreo("ana@example.com");
                requestDTO.setPassword("123456passWORD");
        }

        @Test
        @DisplayName("Listar todos los médicos debe retornar 200 OK")
        void obtenerTodosMedicos() throws Exception {
                when(medicoService.obtenerMedicos()).thenReturn(List.of(responseDTO));

                mockMvc.perform(get("/api/medicos"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].nombres").value("Ana"));
        }

        @Test
        @DisplayName("Listar médicos públicos debe retornar 200 OK")
        void obtenerTodosPublico() throws Exception {
                when(medicoService.obtenerMedicosPublic()).thenReturn(List.of(publicDTO));

                mockMvc.perform(get("/api/medicos/public"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].descripcion").value("Cardióloga"));
        }

        @Test
        @DisplayName("Obtener médico por ID debe retornar 200 OK")
        void obtenerMedicoPorId() throws Exception {
                when(medicoService.obtenerMedicoPorId(1L)).thenReturn(responseDTO);

                mockMvc.perform(get("/api/medicos/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombres").value("Ana"));
        }

        @Test
        @DisplayName("Obtener médico por ID inexistente debe retornar 404")
        void obtenerMedicoPorId_noEncontrado() throws Exception {
                when(medicoService.obtenerMedicoPorId(99L))
                                .thenThrow(new ResourceNotFoundException("Medico no encontrado con ID: 99"));

                mockMvc.perform(get("/api/medicos/99"))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Crear médico debe retornar 201 Created")
        void crearMedico() throws Exception {
                when(medicoService.guardarMedico(any())).thenReturn(responseDTO);

                mockMvc.perform(post("/api/medicos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.nombres").value("Ana"));
        }

        @Test
        @DisplayName("Crear médico con datos inválidos debe retornar 400")
        void crearMedico_datosInvalidos() throws Exception {
                requestDTO.setCorreo(null); // correo requerido

                mockMvc.perform(post("/api/medicos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Actualizar médico debe retornar 200 OK")
        void actualizarMedico() throws Exception {
                when(medicoService.actualizarMedico(any(), any())).thenReturn(responseDTO);

                mockMvc.perform(put("/api/medicos/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombres").value("Ana"));
        }

        @Test
        @DisplayName("Actualizar médico inexistente debe retornar 404")
        void actualizarMedico_noEncontrado() throws Exception {
                when(medicoService.actualizarMedico(any(), any()))
                                .thenThrow(new ResourceNotFoundException("Medico no encontrado"));

                mockMvc.perform(put("/api/medicos/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Eliminar médico debe retornar 204 No Content")
        void eliminarMedico() throws Exception {
                mockMvc.perform(delete("/api/medicos/1"))
                                .andExpect(status().isNoContent());
        }
}
