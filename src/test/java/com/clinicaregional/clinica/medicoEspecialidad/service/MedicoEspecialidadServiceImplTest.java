package com.clinicaregional.clinica.medicoEspecialidad.service;

import com.clinicaregional.clinica.dto.request.MedicoEspecialidadRequest;
import com.clinicaregional.clinica.dto.response.MedicoEspecialidadResponse;
import com.clinicaregional.clinica.entity.*;
import com.clinicaregional.clinica.enums.TipoContrato;
import com.clinicaregional.clinica.enums.TipoMedico;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.MedicoEspecialidadMapper;
import com.clinicaregional.clinica.repository.*;
import com.clinicaregional.clinica.service.impl.MedicoEspecialidadServiceImpl;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MedicoEspecialidadServiceImplTest {

    @Mock
    private MedicoEspecialidadRepository medicoEspecialidadRepository;
    @Mock
    private MedicoRepository medicoRepository;
    @Mock
    private EspecialidadRepository especialidadRepository;
    @Mock
    private FiltroEstado filtroEstado;
    @Mock
    private MedicoEspecialidadMapper mapper;

    @InjectMocks
    private MedicoEspecialidadServiceImpl service;

    private MedicoEspecialidadRequest request;
    private Medico medico;
    private Especialidad especialidad;
    private MedicoEspecialidad relacion;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = new MedicoEspecialidadRequest(1L, 2L, LocalDate.now());
        medico = Medico.builder().id(1L).nombres("Luis").apellidos("Ramirez").numeroColegiatura("12345678901")
                .numeroRNE("987654321").tipoMedico(TipoMedico.ESPECIALISTA).estado(true).build();
        especialidad = Especialidad.builder().id(2L).nombre("Cardiología").estado(true).build();
        relacion = new MedicoEspecialidad(new MedicoEspecialidadId(1L, 2L), LocalDate.now(), medico, especialidad);
    }

    @Test
    @DisplayName("Registrar relación nueva correctamente")
    void registrarRelacionME_nuevaDebeRetornarDTO() {
        when(medicoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(medico));
        when(especialidadRepository.findByIdAndEstadoIsTrue(2L)).thenReturn(Optional.of(especialidad));
        when(medicoEspecialidadRepository.existsByMedicoAndEspecialidad(medico, especialidad)).thenReturn(false);
        when(mapper.toEntity(request, medico, especialidad)).thenReturn(relacion);
        when(medicoEspecialidadRepository.save(any())).thenReturn(relacion);
        when(mapper.toResponse(any())).thenReturn(new MedicoEspecialidadResponse(1L, "Luis Ramirez", "12345678901",
                "987654321", 2L, "Cardiología", LocalDate.now()));

        MedicoEspecialidadResponse result = service.registrarRelacionME(request);
        assertThat(result.getNombreMedico()).isEqualTo("Luis Ramirez");
    }

    @Test
    @DisplayName("Registrar relación existente debe lanzar excepción")
    void registrarRelacionME_existenteDebeLanzarExcepcion() {
        when(medicoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(medico));
        when(especialidadRepository.findByIdAndEstadoIsTrue(2L)).thenReturn(Optional.of(especialidad));
        when(medicoEspecialidadRepository.existsByMedicoAndEspecialidad(medico, especialidad)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.registrarRelacionME(request));
    }

    @Test
    @DisplayName("Actualizar relación existente correctamente")
    void actualizarRelacionME_existenteDebeActualizarFecha() {
        when(medicoEspecialidadRepository.findByIdAndEstadoIsTrue(any())).thenReturn(Optional.of(relacion));
        when(medicoEspecialidadRepository.save(any())).thenReturn(relacion);
        when(mapper.toResponse(any())).thenReturn(new MedicoEspecialidadResponse(1L, "Luis Ramirez", "12345678901",
                "987654321", 2L, "Cardiología", LocalDate.now()));

        MedicoEspecialidadResponse response = service.actualizarRelacionME(1L, 2L, request);
        assertThat(response.getMedicoId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Actualizar relación inexistente debe lanzar excepción")
    void actualizarRelacionME_inexistenteDebeLanzarExcepcion() {
        when(medicoEspecialidadRepository.findByIdAndEstadoIsTrue(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.actualizarRelacionME(1L, 2L, request));
    }

    @Test
    @DisplayName("Eliminar relación existente debe actualizar estado")
    void eliminarRelacionME_existenteDebeActualizarEstado() {
        when(medicoEspecialidadRepository.findByIdAndEstadoIsTrue(any())).thenReturn(Optional.of(relacion));

        service.eliminarRelacionME(1L, 2L);

        verify(medicoEspecialidadRepository).save(any());
        assertThat(relacion.getEstado()).isFalse();
    }

    @Test
    @DisplayName("Eliminar relación inexistente debe lanzar excepción")
    void eliminarRelacionME_inexistenteDebeLanzarExcepcion() {
        when(medicoEspecialidadRepository.findByIdAndEstadoIsTrue(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.eliminarRelacionME(1L, 2L));
    }

    @Test
    @DisplayName("Obtener especialidades de médico con resultados")
    void obtenerEspecialidadDelMedico_conResultados() {
        when(medicoEspecialidadRepository.findByMedicoId(1L)).thenReturn(List.of(relacion));
        when(mapper.toResponse(any())).thenReturn(new MedicoEspecialidadResponse());

        List<MedicoEspecialidadResponse> result = service.obtenerEspecialidadDelMedico(1L);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Obtener especialidades de médico sin resultados debe lanzar excepción")
    void obtenerEspecialidadDelMedico_sinResultadosDebeLanzarExcepcion() {
        when(medicoEspecialidadRepository.findByMedicoId(1L)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> service.obtenerEspecialidadDelMedico(1L));
    }

    @Test
    @DisplayName("Obtener médicos por especialidad con resultados")
    void obtenerMedicosPorEspecialidad_conResultados() {
        when(medicoEspecialidadRepository.findByEspecialidadId(2L)).thenReturn(List.of(relacion));
        when(mapper.toResponse(any())).thenReturn(new MedicoEspecialidadResponse());

        List<MedicoEspecialidadResponse> result = service.obtenerMedicosPorEspecialidad(2L);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Obtener médicos por especialidad sin resultados debe lanzar excepción")
    void obtenerMedicosPorEspecialidad_sinResultadosDebeLanzarExcepcion() {
        when(medicoEspecialidadRepository.findByEspecialidadId(2L)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> service.obtenerMedicosPorEspecialidad(2L));
    }
}
