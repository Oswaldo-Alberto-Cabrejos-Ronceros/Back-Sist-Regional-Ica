package com.clinicaregional.clinica.horario_bloque.service;

import com.clinicaregional.clinica.dto.request.HorarioBloqueRequest;
import com.clinicaregional.clinica.dto.response.HorarioBloqueResponse;
import com.clinicaregional.clinica.entity.Disponibilidad;
import com.clinicaregional.clinica.entity.HorarioBloque;
import com.clinicaregional.clinica.enums.EstadoBloque;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.HorarioBloqueMapper;
import com.clinicaregional.clinica.repository.DisponibilidadRepository;
import com.clinicaregional.clinica.repository.HorarioBloqueRepository;
import com.clinicaregional.clinica.service.EspecialidadService;
import com.clinicaregional.clinica.service.MedicoEspecialidadService;
import com.clinicaregional.clinica.service.impl.HorarioBloqueServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class HorarioBloqueServiceImplTest {

    @Mock
    private HorarioBloqueRepository horarioBloqueRepository;
    @Mock
    private DisponibilidadRepository disponibilidadRepository;
    @Mock
    private HorarioBloqueMapper horarioBloqueMapper;
    @Mock
    private MedicoEspecialidadService medicoEspecialidadService;
    @Mock
    private EspecialidadService especialidadService;

    @InjectMocks
    private HorarioBloqueServiceImpl horarioBloqueService;

    private HorarioBloque bloque;
    private HorarioBloqueResponse response;
    private Disponibilidad disponibilidad;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        disponibilidad = Disponibilidad.builder()
                .id(1L)
                .build();

        bloque = HorarioBloque.builder()
                .id(1L)
                .fecha(LocalDate.now())
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(9, 30))
                .estadoBloque(EstadoBloque.DISPONIBLE)
                .disponibilidad(disponibilidad)
                .build();

        response = new HorarioBloqueResponse(1L,
                bloque.getFecha(),
                bloque.getHoraInicio(),
                bloque.getHoraFin(),
                bloque.getEstadoBloque().name(),
                1L,
                null);
    }

    @Test
    @DisplayName("Debe obtener bloque por ID")
    void obtenerPorId_existente() {

        // Arrange
        when(horarioBloqueRepository.findById(1L)).thenReturn(Optional.of(bloque));
        when(horarioBloqueMapper.mapToHorarioBloqueResponse(bloque)).thenReturn(response);

        // Act
        HorarioBloqueResponse result = horarioBloqueService.obtenerPorId(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si no encuentra bloque")
    void obtenerPorId_noExiste() {

        // Arrange
        when(horarioBloqueRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> horarioBloqueService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("Debe registrar nuevo bloque")
    void registrar_nuevoBloque() {

        // Arrange
        HorarioBloqueRequest request = HorarioBloqueRequest.builder()
                .fecha(bloque.getFecha())
                .horaInicio(bloque.getHoraInicio())
                .horaFin(bloque.getHoraFin())
                .estadoBloque(bloque.getEstadoBloque())
                .disponibilidadId(1L)
                .build();

        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(disponibilidad));
        when(horarioBloqueMapper.mapToHorarioBloque(request)).thenReturn(bloque);
        when(horarioBloqueRepository.save(bloque)).thenReturn(bloque);
        when(horarioBloqueMapper.mapToHorarioBloqueResponse(bloque)).thenReturn(response);

        // Act
        HorarioBloqueResponse result = horarioBloqueService.registrar(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDisponibilidadId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe verificar disponibilidad del bloque")
    void estaDisponible_true() {
        when(horarioBloqueRepository.findById(1L)).thenReturn(Optional.of(bloque));

        boolean disponible = horarioBloqueService.estaDisponible(1L);

        assertThat(disponible).isTrue();
    }
}
