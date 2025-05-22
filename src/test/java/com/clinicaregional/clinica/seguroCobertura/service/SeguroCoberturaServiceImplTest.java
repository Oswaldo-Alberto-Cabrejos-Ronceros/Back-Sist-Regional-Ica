package com.clinicaregional.clinica.seguroCobertura.service;

import com.clinicaregional.clinica.dto.CoberturaDTO;
import com.clinicaregional.clinica.dto.SeguroCoberturaDTO;
import com.clinicaregional.clinica.dto.SeguroDTO;
import com.clinicaregional.clinica.entity.SeguroCobertura;
import com.clinicaregional.clinica.mapper.SeguroCoberturaMapper;
import com.clinicaregional.clinica.repository.SeguroCoberturaRepository;
import com.clinicaregional.clinica.service.CoberturaService;
import com.clinicaregional.clinica.service.SeguroService;
import com.clinicaregional.clinica.service.impl.SeguroCoberturaServiceImpl;
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

class SeguroCoberturaServiceImplTest {

    @Mock
    private SeguroCoberturaRepository seguroCoberturaRepository;

    @Mock
    private SeguroCoberturaMapper seguroCoberturaMapper;

    @Mock
    private SeguroService seguroService;

    @Mock
    private CoberturaService coberturaService;

    @Mock
    private FiltroEstado filtroEstado;

    @InjectMocks
    private SeguroCoberturaServiceImpl service;

    private SeguroCobertura seguroCobertura;
    private SeguroCoberturaDTO dtoEntrada;
    private SeguroDTO seguroDTO;
    private CoberturaDTO coberturaDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dtoEntrada = new SeguroCoberturaDTO(null, 1L, 2L);
        seguroDTO = new SeguroDTO(1L, "RIMAC", "Seguro nacional", "rimac.jpg", null);
        coberturaDTO = new CoberturaDTO(2L, "Cobertura Total", "Incluye todo");

        seguroCobertura = SeguroCobertura.builder()
                .id(10L)
                .estado(true)
                .build();
    }

    @Test
    @DisplayName("Listar todas las relaciones debe retornar lista de DTOs")
    void listarSeguroCobertura() {
        // Arrange
        when(seguroCoberturaRepository.findAll()).thenReturn(List.of(seguroCobertura));
        when(seguroCoberturaMapper.mapToSeguroCoberturaDTO(seguroCobertura)).thenReturn(new SeguroCoberturaDTO(10L, 1L, 2L));

        // Act
        List<SeguroCoberturaDTO> resultado = service.listarSeguroCobertura();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Obtener relación por ID existente")
    void getSeguroCoberturaById_existente() {
        // Arrange
        when(seguroCoberturaRepository.findByIdAndEstadoIsTrue(10L)).thenReturn(Optional.of(seguroCobertura));
        when(seguroCoberturaMapper.mapToSeguroCoberturaDTO(seguroCobertura)).thenReturn(new SeguroCoberturaDTO(10L, 1L, 2L));

        // Act
        Optional<SeguroCoberturaDTO> resultado = service.getSeguroCoberturaById(10L);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Verificar existencia por seguro y cobertura")
    void existsBySeguroAndCobertura_true() {
        // Arrange
        when(seguroCoberturaRepository.existsBySeguro_IdAndCobertura_Id(1L, 2L)).thenReturn(true);

        // Act
        boolean existe = service.existsBySeguroAndCobertura(1L, 2L);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Crear relación válida debe retornar DTO")
    void createSeguroCobertura_valido() {
        // Arrange
        when(seguroService.getSeguroById(1L)).thenReturn(Optional.of(seguroDTO));
        when(coberturaService.getCoberturaById(2L)).thenReturn(Optional.of(coberturaDTO));
        when(seguroCoberturaRepository.existsBySeguro_IdAndCobertura_Id(1L, 2L)).thenReturn(false);
        when(seguroCoberturaMapper.mapToSeguroCobertura(dtoEntrada)).thenReturn(seguroCobertura);
        when(seguroCoberturaRepository.save(seguroCobertura)).thenReturn(seguroCobertura);
        when(seguroCoberturaMapper.mapToSeguroCoberturaDTO(seguroCobertura)).thenReturn(new SeguroCoberturaDTO(10L, 1L, 2L));

        // Act
        SeguroCoberturaDTO creado = service.createSeguroCobertura(dtoEntrada);

        // Assert
        assertThat(creado.getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Crear relación duplicada debe lanzar excepción")
    void createSeguroCobertura_duplicado() {
        // Arrange
        when(seguroService.getSeguroById(1L)).thenReturn(Optional.of(seguroDTO));
        when(coberturaService.getCoberturaById(2L)).thenReturn(Optional.of(coberturaDTO));
        when(seguroCoberturaRepository.existsBySeguro_IdAndCobertura_Id(1L, 2L)).thenReturn(true);

        // Act + Assert
        assertThrows(RuntimeException.class, () -> service.createSeguroCobertura(dtoEntrada));
    }

    @Test
    @DisplayName("Eliminar relación válida actualiza el estado")
    void deleteSeguroCobertura_valido() {
        // Arrange
        seguroCobertura.setEstado(true);
        when(seguroCoberturaRepository.findByIdAndEstadoIsTrue(10L)).thenReturn(Optional.of(seguroCobertura));

        // Act
        service.deleteSeguroCobertura(10L);

        // Assert
        assertThat(seguroCobertura.getEstado()).isFalse();
        verify(seguroCoberturaRepository).save(seguroCobertura);
    }

    @Test
    @DisplayName("Eliminar relación inexistente debe lanzar excepción")
    void deleteSeguroCobertura_inexistente() {
        // Arrange
        when(seguroCoberturaRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class, () -> service.deleteSeguroCobertura(99L));
    }
}
