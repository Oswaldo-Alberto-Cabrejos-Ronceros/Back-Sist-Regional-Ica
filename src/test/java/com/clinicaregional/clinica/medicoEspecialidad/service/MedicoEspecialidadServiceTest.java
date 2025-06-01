package com.clinicaregional.clinica.medicoEspecialidad.service;

import com.clinicaregional.clinica.dto.request.MedicoEspecialidadRequest;
import com.clinicaregional.clinica.dto.response.MedicoEspecialidadResponse;
import com.clinicaregional.clinica.entity.*;
import com.clinicaregional.clinica.enums.TipoContrato;
import com.clinicaregional.clinica.enums.TipoMedico;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MedicoEspecialidadServiceTest {

    @Mock private MedicoEspecialidadRepository medicoEspecialidadRepository;
    @Mock private MedicoRepository medicoRepository;
    @Mock private EspecialidadRepository especialidadRepository;
    @Mock private FiltroEstado filtroEstado;
    @Mock private MedicoEspecialidadMapper mapper;

    @InjectMocks private MedicoEspecialidadServiceImpl service;

    private Medico medico;
    private Especialidad especialidad;
    private MedicoEspecialidadRequest request;
    private MedicoEspecialidad relacion;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        medico = Medico.builder()
                .id(1L)
                .nombres("Luis")
                .apellidos("Ramirez")
                .numeroColegiatura("12345678901")
                .numeroRNE("987654321")
                .tipoContrato(TipoContrato.FIJO)
                .tipoMedico(TipoMedico.ESPECIALISTA)
                .estado(true)
                .build();

        especialidad = Especialidad.builder()
                .id(1L)
                .nombre("Cardiología")
                .estado(true)
                .build();

        request = new MedicoEspecialidadRequest(1L, 1L, LocalDate.now());

        relacion = new MedicoEspecialidad();
        relacion.setId(new MedicoEspecialidadId(1L, 1L));
        relacion.setMedico(medico);
        relacion.setEspecialidad(especialidad);
        relacion.setDesdeFecha(LocalDate.now());
        relacion.setEstado(true);
    }

    @Test
    @DisplayName("Registrar nueva relación exitosamente")
    void registrarRelacionME_exitoso() {
        when(medicoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(medico));
        when(especialidadRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(especialidad));
        when(medicoEspecialidadRepository.existsByMedicoAndEspecialidad(medico, especialidad)).thenReturn(false);
        when(medicoEspecialidadRepository.save(any())).thenReturn(relacion);
        when(mapper.toEntity(request, medico, especialidad)).thenReturn(relacion);
        when(mapper.toResponse(relacion)).thenReturn(new MedicoEspecialidadResponse(1L, "Luis Ramirez", "12345678901", "987654321", 1L, "Cardiología", request.getDesdeFecha()));

        MedicoEspecialidadResponse result = service.registrarRelacionME(request);

        assertThat(result.getMedicoId()).isEqualTo(1L);
        assertThat(result.getNombreMedico()).isEqualTo("Luis Ramirez");
    }

    @Test
    @DisplayName("Registrar relación existente debe lanzar excepción")
    void registrarRelacionME_duplicada_debeLanzarExcepcion() {
        when(medicoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(medico));
        when(especialidadRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(especialidad));
        when(medicoEspecialidadRepository.existsByMedicoAndEspecialidad(medico, especialidad)).thenReturn(true);

        assertThatThrownBy(() -> service.registrarRelacionME(request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("Actualizar relación existente")
    void actualizarRelacionME_exitoso() {
        MedicoEspecialidadId id = new MedicoEspecialidadId(1L, 1L);
        when(medicoEspecialidadRepository.findByIdAndEstadoIsTrue(id)).thenReturn(Optional.of(relacion));
        when(medicoEspecialidadRepository.save(any())).thenReturn(relacion);
        when(mapper.toResponse(any())).thenReturn(new MedicoEspecialidadResponse(1L, "Luis Ramirez", "12345678901", "987654321", 1L, "Cardiología", request.getDesdeFecha()));

        MedicoEspecialidadResponse result = service.actualizarRelacionME(1L, 1L, request);

        assertThat(result.getDesdeFecha()).isEqualTo(request.getDesdeFecha());
    }

    @Test
    @DisplayName("Eliminar relación debe cambiar estado")
    void eliminarRelacionME_debeActualizarEstado() {
        MedicoEspecialidadId id = new MedicoEspecialidadId(1L, 1L);
        when(medicoEspecialidadRepository.findByIdAndEstadoIsTrue(id)).thenReturn(Optional.of(relacion));

        service.eliminarRelacionME(1L, 1L);

        assertThat(relacion.getEstado()).isFalse();
        verify(medicoEspecialidadRepository).save(relacion);
    }

    @Test
    @DisplayName("Obtener especialidades de un médico")
    void obtenerEspecialidadDelMedico_debeRetornarLista() {
        when(medicoEspecialidadRepository.findByMedicoId(1L)).thenReturn(List.of(relacion));
        when(mapper.toResponse(any())).thenReturn(new MedicoEspecialidadResponse());

        List<MedicoEspecialidadResponse> resultado = service.obtenerEspecialidadDelMedico(1L);

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Obtener médicos de una especialidad")
    void obtenerMedicosPorEspecialidad_debeRetornarLista() {
        when(medicoEspecialidadRepository.findByEspecialidadId(1L)).thenReturn(List.of(relacion));
        when(mapper.toResponse(any())).thenReturn(new MedicoEspecialidadResponse());

        List<MedicoEspecialidadResponse> resultado = service.obtenerMedicosPorEspecialidad(1L);

        assertThat(resultado).hasSize(1);
    }
}
