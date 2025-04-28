package com.clinicaregional.clinica.especialidades.service;

import com.clinicaregional.clinica.dto.request.EspecialidadRequest;
import com.clinicaregional.clinica.dto.response.EspecialidadResponse;
import com.clinicaregional.clinica.entity.Especialidad;
import com.clinicaregional.clinica.mapper.EspecialidadMapper;
import com.clinicaregional.clinica.repository.EspecialidadRepository;
import com.clinicaregional.clinica.service.impl.EspecialidadServiceImpl;
import com.clinicaregional.clinica.util.FiltroEstado;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EspecialidadServiceImplTest {

    @Mock
    private EspecialidadRepository especialidadRepository;

    @Mock
    private FiltroEstado filtroEstado;

    @InjectMocks
    private EspecialidadServiceImpl especialidadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Listar todas las especialidades activas")
    void listarEspecialidades_debeRetornarLista() {
        Especialidad especialidad = Especialidad.builder()
                .id(1L)
                .nombre("Cardiología")
                .descripcion("Especialidad del corazón")
                .imagen("cardio.jpg")
                .estado(true)
                .build();
        when(especialidadRepository.findAll()).thenReturn(List.of(especialidad));

        List<EspecialidadResponse> resultado = especialidadService.listarEspecialidades();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Cardiología");
    }

    @Test
    @DisplayName("Obtener especialidad por ID existente")
    void getEspecialidadById_existente() {
        Especialidad especialidad = Especialidad.builder()
                .id(1L)
                .nombre("Cardiología")
                .descripcion("Especialidad del corazón")
                .imagen("cardio.jpg")
                .estado(true)
                .build();
        when(especialidadRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(especialidad));

        Optional<EspecialidadResponse> resultado = especialidadService.getEspecialidadById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Cardiología");
    }

    @Test
    @DisplayName("Guardar nueva especialidad exitosamente")
    void guardarEspecialidad_exitoso() {
        EspecialidadRequest request = new EspecialidadRequest("Neurología", "Especialidad del cerebro", "neuro.jpg");
        Especialidad especialidad = EspecialidadMapper.toEntity(request);
        Especialidad especialidadGuardada = Especialidad.builder()
                .id(1L)
                .nombre("Neurología")
                .descripcion("Especialidad del cerebro")
                .imagen("neuro.jpg")
                .estado(true)
                .build();
        when(especialidadRepository.existsByNombre("Neurología")).thenReturn(false);
        when(especialidadRepository.save(any(Especialidad.class))).thenReturn(especialidadGuardada);

        EspecialidadResponse resultado = especialidadService.guardarEspecialidad(request);

        assertThat(resultado.getNombre()).isEqualTo("Neurología");
    }

    @Test
    @DisplayName("Guardar especialidad con nombre repetido debe lanzar excepción")
    void guardarEspecialidad_nombreDuplicado_debeLanzarExcepcion() {
        EspecialidadRequest request = new EspecialidadRequest("Cardiología", "Especialidad del corazón", "cardio.jpg");
        when(especialidadRepository.existsByNombre("Cardiología")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> especialidadService.guardarEspecialidad(request));
    }

    @Test
    @DisplayName("Actualizar especialidad existente exitosamente")
    void actualizarEspecialidad_existente() {
        Especialidad especialidadExistente = Especialidad.builder()
                .id(1L)
                .nombre("Cardiología")
                .descripcion("Especialidad del corazón")
                .imagen("cardio.jpg")
                .estado(true)
                .build();
        EspecialidadRequest request = new EspecialidadRequest("Neurología", "Especialidad del cerebro", "neuro.jpg");
        when(especialidadRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(especialidadExistente));
        when(especialidadRepository.existsByNombre("Neurología")).thenReturn(false);
        when(especialidadRepository.save(any(Especialidad.class))).thenReturn(especialidadExistente);

        EspecialidadResponse resultado = especialidadService.actualizarEspecialidad(1L, request);

        assertThat(resultado.getNombre()).isEqualTo("Neurología");
    }

    @Test
    @DisplayName("Actualizar especialidad inexistente debe lanzar excepción")
    void actualizarEspecialidad_inexistente() {
        EspecialidadRequest request = new EspecialidadRequest("Neurología", "Especialidad del cerebro", "neuro.jpg");
        when(especialidadRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> especialidadService.actualizarEspecialidad(99L, request));
    }

    @Test
    @DisplayName("Eliminar especialidad existente exitosamente")
    void eliminarEspecialidad_existente() {
        Especialidad especialidadExistente = Especialidad.builder()
                .id(1L)
                .nombre("Cardiología")
                .descripcion("Especialidad del corazón")
                .imagen("cardio.jpg")
                .estado(true)
                .build();
        when(especialidadRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(especialidadExistente));

        especialidadService.eliminarEspecialidad(1L);

        assertThat(especialidadExistente.getEstado()).isFalse();
        verify(especialidadRepository, times(1)).save(especialidadExistente);
    }

    @Test
    @DisplayName("Eliminar especialidad inexistente debe lanzar excepción")
    void eliminarEspecialidad_inexistente() {
        when(especialidadRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> especialidadService.eliminarEspecialidad(99L));
    }
}
