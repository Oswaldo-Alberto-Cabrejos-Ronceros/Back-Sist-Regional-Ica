package com.clinicaregional.clinica.seguro.service;

import com.clinicaregional.clinica.dto.SeguroDTO;
import com.clinicaregional.clinica.entity.Seguro;
import com.clinicaregional.clinica.enums.EstadoSeguro;
import com.clinicaregional.clinica.mapper.SeguroMapper;
import com.clinicaregional.clinica.repository.SeguroRepository;
import com.clinicaregional.clinica.service.impl.SeguroServiceImpl;
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
import static org.mockito.Mockito.*;

class SeguroServiceImplTest {

    @Mock
    private SeguroRepository seguroRepository;

    @Mock
    private SeguroMapper seguroMapper;

    @Mock
    private FiltroEstado filtroEstado;

    @InjectMocks
    private SeguroServiceImpl seguroService;

    private Seguro seguro;
    private SeguroDTO seguroDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        seguro = Seguro.builder()
                .id(1L)
                .nombre("RIMAC")
                .descripcion("Cobertura nacional")
                .imagenUrl("rimac.jpg")
                .estadoSeguro(EstadoSeguro.ACTIVO)
                .estado(true)
                .build();

        seguroDTO = new SeguroDTO(1L, "RIMAC", "Cobertura nacional", "rimac.jpg", EstadoSeguro.ACTIVO);
    }

    @Test
    @DisplayName("Listar seguros activos")
    void listarSeguros_debeRetornarLista() {
        // Arrange
        when(seguroRepository.findAll()).thenReturn(List.of(seguro));
        when(seguroMapper.mapToSeguroDTO(any())).thenReturn(seguroDTO);

        // Act
        List<SeguroDTO> seguros = seguroService.listarSeguros();

        // Assert
        assertThat(seguros).hasSize(1);
        assertThat(seguros.get(0).getNombre()).isEqualTo("RIMAC");
    }

    @Test
    @DisplayName("Obtener seguro por ID existente")
    void getSeguroById_existente() {
        // Arrange
        when(seguroRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(seguro));
        when(seguroMapper.mapToSeguroDTO(seguro)).thenReturn(seguroDTO);

        // Act
        Optional<SeguroDTO> result = seguroService.getSeguroById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getNombre()).isEqualTo("RIMAC");
    }

    @Test
    @DisplayName("Crear seguro correctamente")
    void createSeguro_debeRetornarDTO() {
        // Arrange
        SeguroDTO dtoEntrada = new SeguroDTO(null, "SIS", "Seguro público", "sis.jpg", EstadoSeguro.ACTIVO);
        Seguro nuevoSeguro = Seguro.builder()
                .nombre("SIS")
                .descripcion("Seguro público")
                .imagenUrl("sis.jpg")
                .estadoSeguro(EstadoSeguro.ACTIVO)
                .build();
        when(seguroRepository.existsByNombre("SIS")).thenReturn(false);
        when(seguroMapper.mapToSeguro(dtoEntrada)).thenReturn(nuevoSeguro);
        when(seguroRepository.save(nuevoSeguro)).thenReturn(seguro);
        when(seguroMapper.mapToSeguroDTO(seguro)).thenReturn(seguroDTO);

        // Act
        SeguroDTO resultado = seguroService.createSeguro(dtoEntrada);

        // Assert
        assertThat(resultado.getNombre()).isEqualTo("RIMAC");
    }

    @Test
    @DisplayName("Crear seguro con nombre duplicado lanza excepción")
    void createSeguro_nombreDuplicado() {
        // Arrange
        when(seguroRepository.existsByNombre("RIMAC")).thenReturn(true);

        // Act + Assert
        assertThrows(RuntimeException.class, () -> seguroService.createSeguro(seguroDTO));
    }

    @Test
    @DisplayName("Actualizar seguro correctamente")
    void updateSeguro_existente() {
        // Arrange
        when(seguroRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(seguro));
        when(seguroRepository.existsByNombre("RIMAC")).thenReturn(false);
        when(seguroRepository.save(any())).thenReturn(seguro);
        when(seguroMapper.mapToSeguroDTO(any())).thenReturn(seguroDTO);

        // Act
        SeguroDTO actualizado = seguroService.updateSeguro(1L, seguroDTO);

        // Assert
        assertThat(actualizado.getDescripcion()).isEqualTo("Cobertura nacional");
    }

    @Test
    @DisplayName("Actualizar seguro con ID inexistente lanza excepción")
    void updateSeguro_idInexistente() {
        // Arrange
        when(seguroRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class, () -> seguroService.updateSeguro(99L, seguroDTO));
    }

    @Test
    @DisplayName("Actualizar seguro con nombre duplicado lanza excepción")
    void updateSeguro_nombreDuplicado() {
        // Arrange
        when(seguroRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(seguro));
        when(seguroRepository.existsByNombre("RIMAC")).thenReturn(true);

        // Act + Assert
        assertThrows(RuntimeException.class, () -> seguroService.updateSeguro(1L, seguroDTO));
    }

    @Test
    @DisplayName("Actualizar estado del seguro correctamente")
    void updateEstadoSeguro_correctamente() {
        // Arrange
        when(seguroRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(seguro));
        when(seguroRepository.save(any())).thenReturn(seguro);
        when(seguroMapper.mapToSeguroDTO(any())).thenReturn(seguroDTO);

        // Act
        SeguroDTO actualizado = seguroService.updateEstadoSeguro(1L, EstadoSeguro.INACTIVO);

        // Assert
        assertThat(actualizado.getEstadoSeguro()).isEqualTo(EstadoSeguro.ACTIVO);
        verify(seguroRepository).save(any());
    }

    @Test
    @DisplayName("Actualizar estado del seguro con el mismo valor lanza excepción")
    void updateEstadoSeguro_mismoEstado() {
        // Arrange
        when(seguroRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(seguro));

        // Act + Assert
        assertThrows(RuntimeException.class, () -> seguroService.updateEstadoSeguro(1L, EstadoSeguro.ACTIVO));
    }

    @Test
    @DisplayName("Eliminar seguro correctamente")
    void deleteSeguro_existente() {
        // Arrange
        when(seguroRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(seguro));

        // Act
        seguroService.deleteSeguro(1L);

        // Assert
        assertThat(seguro.getEstado()).isFalse();
        verify(seguroRepository).save(seguro);
    }

    @Test
    @DisplayName("Eliminar seguro con ID inexistente lanza excepción")
    void deleteSeguro_inexistente() {
        // Arrange
        when(seguroRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class, () -> seguroService.deleteSeguro(99L));
    }
}
