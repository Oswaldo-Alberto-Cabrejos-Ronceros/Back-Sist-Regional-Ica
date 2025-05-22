package com.clinicaregional.clinica.cobertura.service;

import com.clinicaregional.clinica.dto.CoberturaDTO;
import com.clinicaregional.clinica.entity.Cobertura;
import com.clinicaregional.clinica.mapper.CoberturaMapper;
import com.clinicaregional.clinica.repository.CoberturaRepository;
import com.clinicaregional.clinica.service.impl.CoberturaServiceImpl;
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

class CoberturaServiceImplTest {

    @Mock
    private CoberturaRepository coberturaRepository;

    @Mock
    private CoberturaMapper coberturaMapper;

    @Mock
    private FiltroEstado filtroEstado;

    @InjectMocks
    private CoberturaServiceImpl coberturaService;

    private Cobertura cobertura;
    private CoberturaDTO coberturaDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cobertura = Cobertura.builder()
                .id(1L)
                .nombre("Cobertura Total")
                .descripcion("Incluye todos los beneficios")
                .estado(true)
                .build();

        coberturaDTO = new CoberturaDTO(1L, "Cobertura Total", "Incluye todos los beneficios");
    }

    @Test
    @DisplayName("Listar coberturas debe retornar lista")
    void listarCoberturas_debeRetornarLista() {
        // Arrange
        when(coberturaRepository.findAll()).thenReturn(List.of(cobertura));
        when(coberturaMapper.mapToCoberturaDTO(cobertura)).thenReturn(coberturaDTO);

        // Act
        List<CoberturaDTO> lista = coberturaService.listarCoberturas();

        // Assert
        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getNombre()).isEqualTo("Cobertura Total");
    }

    @Test
    @DisplayName("Obtener cobertura por ID existente")
    void getCoberturaById_existente() {
        // Arrange
        when(coberturaRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(cobertura));
        when(coberturaMapper.mapToCoberturaDTO(cobertura)).thenReturn(coberturaDTO);

        // Act
        Optional<CoberturaDTO> resultado = coberturaService.getCoberturaById(1L);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getDescripcion()).isEqualTo("Incluye todos los beneficios");
    }

    @Test
    @DisplayName("Crear cobertura correctamente")
    void createCobertura_valida() {
        // Arrange
        when(coberturaRepository.existsByNombre("Cobertura Total")).thenReturn(false);
        when(coberturaMapper.mapToCobertura(coberturaDTO)).thenReturn(cobertura);
        when(coberturaRepository.save(cobertura)).thenReturn(cobertura);
        when(coberturaMapper.mapToCoberturaDTO(cobertura)).thenReturn(coberturaDTO);

        // Act
        CoberturaDTO result = coberturaService.createCobertura(coberturaDTO);

        // Assert
        assertThat(result.getNombre()).isEqualTo("Cobertura Total");
    }

    @Test
    @DisplayName("Crear cobertura con nombre duplicado lanza excepción")
    void createCobertura_nombreDuplicado() {
        // Arrange
        when(coberturaRepository.existsByNombre("Cobertura Total")).thenReturn(true);

        // Act + Assert
        assertThrows(RuntimeException.class, () -> coberturaService.createCobertura(coberturaDTO));
    }

    @Test
    @DisplayName("Actualizar cobertura correctamente")
    void updateCobertura_valida() {
        // Arrange
        when(coberturaRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(cobertura));
        when(coberturaRepository.existsByNombre("Cobertura Total")).thenReturn(false);
        when(coberturaRepository.save(any())).thenReturn(cobertura);
        when(coberturaMapper.mapToCoberturaDTO(any())).thenReturn(coberturaDTO);

        // Act
        CoberturaDTO actualizada = coberturaService.updateCobertura(1L, coberturaDTO);

        // Assert
        assertThat(actualizada.getDescripcion()).isEqualTo("Incluye todos los beneficios");
    }

    @Test
    @DisplayName("Actualizar cobertura inexistente lanza excepción")
    void updateCobertura_inexistente() {
        // Arrange
        when(coberturaRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class, () -> coberturaService.updateCobertura(99L, coberturaDTO));
    }

    @Test
    @DisplayName("Actualizar cobertura con nombre duplicado lanza excepción")
    void updateCobertura_nombreDuplicado() {
        // Arrange
        when(coberturaRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(cobertura));
        when(coberturaRepository.existsByNombre("Cobertura Total")).thenReturn(true);

        // Act + Assert
        assertThrows(RuntimeException.class, () -> coberturaService.updateCobertura(1L, coberturaDTO));
    }

    @Test
    @DisplayName("Eliminar cobertura correctamente")
    void deleteCobertura_valida() {
        // Arrange
        when(coberturaRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(cobertura));

        // Act
        coberturaService.deleteCobertura(1L);

        // Assert
        assertThat(cobertura.getEstado()).isFalse();
        verify(coberturaRepository).save(cobertura);
    }

    @Test
    @DisplayName("Eliminar cobertura inexistente lanza excepción")
    void deleteCobertura_inexistente() {
        // Arrange
        when(coberturaRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class, () -> coberturaService.deleteCobertura(99L));
    }
}
