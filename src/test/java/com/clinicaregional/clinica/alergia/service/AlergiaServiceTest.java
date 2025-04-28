package com.clinicaregional.clinica.alergia.service;

import com.clinicaregional.clinica.dto.AlergiaDTO;
import com.clinicaregional.clinica.entity.Alergia;
import com.clinicaregional.clinica.enums.TipoAlergia;
import com.clinicaregional.clinica.mapper.AlergiaMapper;
import com.clinicaregional.clinica.repository.AlergiaRepository;
import com.clinicaregional.clinica.service.impl.AlergiaServiceImpl;
import com.clinicaregional.clinica.util.FiltroEstado;
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

class AlergiaServiceTest {

    @Mock
    private AlergiaRepository alergiaRepository;

    @Mock
    private AlergiaMapper alergiaMapper;

    @Mock
    private FiltroEstado filtroEstado;

    @InjectMocks
    private AlergiaServiceImpl alergiaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Listar todas las alergias activas")
    void listarAlergias_debeRetornarListaDeAlergias() {
        // Arrange
        Alergia alergia = Alergia.builder()
                .id(1L)
                .nombre("Polvo")
                .tipoAlergia(TipoAlergia.AMBIENTAL)
                .estado(true)
                .build();
        when(alergiaRepository.findAll()).thenReturn(List.of(alergia));
        when(alergiaMapper.mapToAlergiaDTO(alergia)).thenReturn(new AlergiaDTO(1L, "Polvo", TipoAlergia.AMBIENTAL));

        // Act
        List<AlergiaDTO> resultado = alergiaService.listarAlergias();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Polvo");
    }

    @Test
    @DisplayName("Obtener alergia por ID existente")
    void obtenerAlergiaPorId_existente_debeRetornarDTO() {
        // Arrange
        Alergia alergia = Alergia.builder()
                .id(1L)
                .nombre("Polvo")
                .tipoAlergia(TipoAlergia.AMBIENTAL)
                .estado(true)
                .build();
        when(alergiaRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(alergia));
        when(alergiaMapper.mapToAlergiaDTO(alergia)).thenReturn(new AlergiaDTO(1L, "Polvo", TipoAlergia.AMBIENTAL));

        // Act
        Optional<AlergiaDTO> resultado = alergiaService.getAlergiaPorId(1L);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Polvo");
    }

    @Test
    @DisplayName("Guardar alergia nueva correctamente")
    void guardarAlergia_nueva_debeRetornarDTO() {
        // Arrange
        AlergiaDTO dtoEntrada = new AlergiaDTO(null, "Polen", TipoAlergia.AMBIENTAL);
        Alergia alergia = Alergia.builder()
                .nombre("Polen")
                .tipoAlergia(TipoAlergia.AMBIENTAL)
                .estado(true)
                .build();
        when(alergiaMapper.mapToAlergia(dtoEntrada)).thenReturn(alergia);
        when(alergiaRepository.existsByNombreAndEstadoIsTrue("Polen")).thenReturn(false);
        when(alergiaRepository.save(alergia)).thenReturn(
                Alergia.builder()
                        .id(1L)
                        .nombre("Polen")
                        .tipoAlergia(TipoAlergia.AMBIENTAL)
                        .estado(true)
                        .build()
        );
        when(alergiaMapper.mapToAlergiaDTO(any())).thenReturn(new AlergiaDTO(1L, "Polen", TipoAlergia.AMBIENTAL));

        // Act
        AlergiaDTO resultado = alergiaService.crearAlergia(dtoEntrada);

        // Assert
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Polen");
    }

    @Test
    @DisplayName("Guardar alergia existente debe lanzar excepción")
    void guardarAlergia_existente_debeLanzarExcepcion() {
        // Arrange
        AlergiaDTO dtoEntrada = new AlergiaDTO(null, "Polvo", TipoAlergia.AMBIENTAL);
        when(alergiaRepository.existsByNombreAndEstadoIsTrue("Polvo")).thenReturn(true);

        // Act + Assert
        assertThrows(RuntimeException.class, () -> alergiaService.crearAlergia(dtoEntrada));
    }

    @Test
    @DisplayName("Actualizar alergia existente correctamente")
    void actualizarAlergia_existente_debeActualizarDatos() {
        // Arrange
        Alergia alergiaExistente = Alergia.builder()
                .id(1L)
                .nombre("Polvo")
                .tipoAlergia(TipoAlergia.AMBIENTAL)
                .estado(true)
                .build();
        when(alergiaRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(alergiaExistente));
        when(alergiaRepository.existsByNombreAndEstadoIsTrue("NuevoNombre")).thenReturn(false);
        when(alergiaRepository.save(any())).thenReturn(alergiaExistente);
        when(alergiaMapper.mapToAlergiaDTO(any())).thenReturn(new AlergiaDTO(1L, "NuevoNombre", TipoAlergia.AMBIENTAL));

        AlergiaDTO nuevoDTO = new AlergiaDTO(1L, "NuevoNombre", TipoAlergia.AMBIENTAL);

        // Act
        AlergiaDTO resultado = alergiaService.updateAlergia(1L, nuevoDTO);

        // Assert
        assertThat(resultado.getNombre()).isEqualTo("NuevoNombre");
    }

    @Test
    @DisplayName("Eliminar alergia existente debe actualizar el estado a false")
    void eliminarAlergia_existente_debeActualizarEstado() {
        // Arrange
        Alergia alergia = Alergia.builder()
                .id(1L)
                .nombre("Polvo")
                .tipoAlergia(TipoAlergia.AMBIENTAL)
                .estado(true)
                .build();
        when(alergiaRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(alergia));

        // Act
        alergiaService.eliminarAlergia(1L);

        // Assert
        assertThat(alergia.getEstado()).isFalse();
        verify(alergiaRepository, times(1)).save(alergia);
    }
}
