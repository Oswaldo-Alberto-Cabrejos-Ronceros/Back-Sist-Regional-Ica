package com.clinicaregional.clinica.tipoDocumento.service;

import com.clinicaregional.clinica.dto.TipoDocumentoDTO;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.mapper.TipoDocumentoMapper;
import com.clinicaregional.clinica.repository.TipoDocumentoRepository;
import com.clinicaregional.clinica.service.impl.TipoDocumentoServiceImpl;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoDocumentoServiceTest {

    @Mock
    private TipoDocumentoRepository tipoDocumentoRepository;

    @Mock
    private TipoDocumentoMapper tipoDocumentoMapper;

    @Mock
    private FiltroEstado filtroEstado;

    @InjectMocks
    private TipoDocumentoServiceImpl tipoDocumentoService;

    private TipoDocumento tipoDocumento;
    private TipoDocumentoDTO tipoDocumentoDTO;

    @BeforeEach
    void setUp() {
        tipoDocumento = TipoDocumento.builder()
                .id(1L)
                .nombre("DNI")
                .descripcion("Documento Nacional de Identidad")
                .estado(true)
                .build();

        tipoDocumentoDTO = TipoDocumentoDTO.builder()
                .id(1L)
                .nombre("DNI")
                .descripcion("Documento Nacional de Identidad")
                .build();

        doNothing().when(filtroEstado).activarFiltroEstado(true);
    }

    @Test
    void listarTipoDocumento_exitoso() {
        // Arrange
        when(tipoDocumentoRepository.findAll()).thenReturn(List.of(tipoDocumento));
        when(tipoDocumentoMapper.mapToTipoDocumentoDTO(any(TipoDocumento.class))).thenReturn(tipoDocumentoDTO);

        // Act
        List<TipoDocumentoDTO> documentos = tipoDocumentoService.listarTipoDocumento();

        // Assert
        assertThat(documentos).hasSize(1);
        assertThat(documentos.get(0).getNombre()).isEqualTo("DNI");
        verify(tipoDocumentoRepository, times(1)).findAll();
    }

    @Test
    void getTipoDocumentoById_existente() {
        // Arrange
        when(tipoDocumentoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(tipoDocumento));
        when(tipoDocumentoMapper.mapToTipoDocumentoDTO(any(TipoDocumento.class))).thenReturn(tipoDocumentoDTO);

        // Act
        Optional<TipoDocumentoDTO> result = tipoDocumentoService.getTipoDocumentoById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getNombre()).isEqualTo("DNI");
    }

    @Test
    void getTipoDocumentoById_noExistente() {
        // Arrange
        when(tipoDocumentoRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        // Act
        Optional<TipoDocumentoDTO> result = tipoDocumentoService.getTipoDocumentoById(99L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void createTipoDocumento_exitoso() {
        // Arrange
        when(tipoDocumentoRepository.existsByNombreAndEstadoIsTrue(anyString())).thenReturn(false);
        when(tipoDocumentoMapper.mapToTipoDocumento(any(TipoDocumentoDTO.class))).thenReturn(tipoDocumento);
        when(tipoDocumentoRepository.save(any(TipoDocumento.class))).thenReturn(tipoDocumento);
        when(tipoDocumentoMapper.mapToTipoDocumentoDTO(any(TipoDocumento.class))).thenReturn(tipoDocumentoDTO);

        // Act
        TipoDocumentoDTO result = tipoDocumentoService.createTipoDocumento(tipoDocumentoDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("DNI");
        verify(tipoDocumentoRepository, times(1)).save(any(TipoDocumento.class));
    }

    @Test
    void createTipoDocumento_nombreExistente_deberiaLanzarExcepcion() {
        // Arrange
        when(tipoDocumentoRepository.existsByNombreAndEstadoIsTrue(anyString())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> tipoDocumentoService.createTipoDocumento(tipoDocumentoDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El tipo de documento ya existe en el sistema");
    }

    @Test
    void updateTipoDocumento_exitoso() {
        // Arrange
        when(tipoDocumentoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(tipoDocumento));
        when(tipoDocumentoRepository.existsByNombreAndEstadoIsTrue(anyString())).thenReturn(false);
        when(tipoDocumentoRepository.save(any(TipoDocumento.class))).thenReturn(tipoDocumento);
        when(tipoDocumentoMapper.mapToTipoDocumentoDTO(any(TipoDocumento.class))).thenReturn(tipoDocumentoDTO);

        // Act
        TipoDocumentoDTO actualizado = tipoDocumentoService.updateTipoDocumento(1L, tipoDocumentoDTO);

        // Assert
        assertThat(actualizado.getNombre()).isEqualTo("DNI");
        verify(tipoDocumentoRepository, times(1)).save(any(TipoDocumento.class));
    }

    @Test
    void updateTipoDocumento_noExistente_deberiaLanzarExcepcion() {
        // Arrange
        when(tipoDocumentoRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tipoDocumentoService.updateTipoDocumento(99L, tipoDocumentoDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe un tipo de documento con el id");
    }

    @Test
    void deleteTipoDocumento_exitoso() {
        // Arrange
        TipoDocumento tipoDocumentoExistente = TipoDocumento.builder()
                .id(1L)
                .nombre("DNI")
                .descripcion("Documento Nacional de Identidad")
                .estado(true)
                .build();

        when(tipoDocumentoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(tipoDocumentoExistente));

        // Act
        tipoDocumentoService.deleteTipoDocumento(1L);

        // Assert
        assertThat(tipoDocumentoExistente.getEstado()).isFalse();
        verify(tipoDocumentoRepository, times(1)).save(tipoDocumentoExistente);
    }
}
