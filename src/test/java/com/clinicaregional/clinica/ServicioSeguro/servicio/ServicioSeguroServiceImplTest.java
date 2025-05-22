package com.clinicaregional.clinica.ServicioSeguro.servicio;

import com.clinicaregional.clinica.dto.CoberturaDTO;
import com.clinicaregional.clinica.dto.ServicioSeguroDTO;
import com.clinicaregional.clinica.dto.SeguroDTO;
import com.clinicaregional.clinica.entity.Servicio;
import com.clinicaregional.clinica.entity.ServicioSeguro;
import com.clinicaregional.clinica.mapper.ServicioSeguroMapper;
import com.clinicaregional.clinica.repository.ServicioRepository;
import com.clinicaregional.clinica.repository.ServicioSeguroRepository;
import com.clinicaregional.clinica.service.CoberturaService;
import com.clinicaregional.clinica.service.SeguroCoberturaService;
import com.clinicaregional.clinica.service.SeguroService;
import com.clinicaregional.clinica.service.impl.ServicioSeguroServiceImpl;
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

class ServicioSeguroServiceImplTest {

    @Mock
    private ServicioSeguroRepository servicioSeguroRepository;

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private ServicioSeguroMapper servicioSeguroMapper;

    @Mock
    private SeguroService seguroService;

    @Mock
    private CoberturaService coberturaService;

    @Mock
    private SeguroCoberturaService seguroCoberturaService;

    @Mock
    private FiltroEstado filtroEstado;

    @InjectMocks
    private ServicioSeguroServiceImpl servicio;

    private ServicioSeguroDTO dtoEntrada;
    private Servicio servicioEntity;
    private ServicioSeguro servicioSeguroEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dtoEntrada = new ServicioSeguroDTO(null, 1L, 2L, 3L);
        servicioEntity = Servicio.builder().id(1L).estado(true).build();
        servicioSeguroEntity = ServicioSeguro.builder().id(10L).servicio(servicioEntity).estado(true).build();
    }

    @Test
    @DisplayName("Listar todos los ServicioSeguro activos")
    void listarServicioSeguro_debeRetornarListaDTOs() {
        // Arrange
        when(servicioSeguroRepository.findAll()).thenReturn(List.of(servicioSeguroEntity));
        when(servicioSeguroMapper.mapToServicioSeguroDTO(any())).thenReturn(new ServicioSeguroDTO(10L, 1L, 2L, 3L));

        // Act
        List<ServicioSeguroDTO> resultado = servicio.listarServicioSeguro();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Obtener ServicioSeguro por ID existente")
    void getSeguroServicioById_existente() {
        // Arrange
        when(servicioSeguroRepository.findByIdAndEstadoIsTrue(10L)).thenReturn(Optional.of(servicioSeguroEntity));
        when(servicioSeguroMapper.mapToServicioSeguroDTO(any())).thenReturn(new ServicioSeguroDTO(10L, 1L, 2L, 3L));

        // Act
        Optional<ServicioSeguroDTO> resultado = servicio.getSeguroServicioById(10L);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Crear ServicioSeguro válido")
    void createServicioSeguro_valido() {
        // Arrange
        when(servicioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(servicioEntity));
        when(seguroService.getSeguroById(2L)).thenReturn(Optional.of(new SeguroDTO()));
        when(coberturaService.getCoberturaById(3L)).thenReturn(Optional.of(new CoberturaDTO()));
        when(seguroCoberturaService.existsBySeguroAndCobertura(2L, 3L)).thenReturn(true);
        when(servicioSeguroRepository.existsByServicio_IdAndSeguro_IdAndCobertura_Id(1L, 2L, 3L)).thenReturn(false);
        when(servicioSeguroMapper.mapToServicioSeguro(dtoEntrada)).thenReturn(servicioSeguroEntity);
        when(servicioSeguroRepository.save(any())).thenReturn(servicioSeguroEntity);
        when(servicioSeguroMapper.mapToServicioSeguroDTO(servicioSeguroEntity)).thenReturn(new ServicioSeguroDTO(10L, 1L, 2L, 3L));

        // Act
        ServicioSeguroDTO creado = servicio.createServicioSeguro(dtoEntrada);

        // Assert
        assertThat(creado.getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Crear ServicioSeguro duplicado debe lanzar excepción")
    void createServicioSeguro_duplicado() {
        // Arrange
        when(servicioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(servicioEntity));
        when(seguroService.getSeguroById(2L)).thenReturn(Optional.of(new SeguroDTO()));
        when(coberturaService.getCoberturaById(3L)).thenReturn(Optional.of(new CoberturaDTO()));
        when(seguroCoberturaService.existsBySeguroAndCobertura(2L, 3L)).thenReturn(true);
        when(servicioSeguroRepository.existsByServicio_IdAndSeguro_IdAndCobertura_Id(1L, 2L, 3L)).thenReturn(true);

        // Act + Assert
        assertThrows(RuntimeException.class, () -> servicio.createServicioSeguro(dtoEntrada));
    }

    @Test
    @DisplayName("Crear ServicioSeguro con seguro y cobertura no relacionados debe lanzar excepción")
    void createServicioSeguro_sinRelacionSegCob() {
        // Arrange
        when(servicioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(servicioEntity));
        when(seguroService.getSeguroById(2L)).thenReturn(Optional.of(new SeguroDTO()));
        when(coberturaService.getCoberturaById(3L)).thenReturn(Optional.of(new CoberturaDTO()));
        when(seguroCoberturaService.existsBySeguroAndCobertura(2L, 3L)).thenReturn(false);

        // Act + Assert
        assertThrows(RuntimeException.class, () -> servicio.createServicioSeguro(dtoEntrada));
    }

    @Test
    @DisplayName("Eliminar ServicioSeguro válido cambia estado a false")
    void deleteServicioSeguro_valido() {
        // Arrange
        when(servicioSeguroRepository.findByIdAndEstadoIsTrue(10L)).thenReturn(Optional.of(servicioSeguroEntity));

        // Act
        servicio.deleteServicioSeguro(10L);

        // Assert
        assertThat(servicioSeguroEntity.getEstado()).isFalse();
        verify(servicioSeguroRepository).save(servicioSeguroEntity);
    }

    @Test
    @DisplayName("Eliminar ServicioSeguro inexistente lanza excepción")
    void deleteServicioSeguro_inexistente() {
        // Arrange
        when(servicioSeguroRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class, () -> servicio.deleteServicioSeguro(99L));
    }
}
