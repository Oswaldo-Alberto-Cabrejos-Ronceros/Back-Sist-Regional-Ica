package com.clinicaregional.clinica.servicios.service;

import com.clinicaregional.clinica.dto.request.ServicioRequest;
import com.clinicaregional.clinica.dto.response.ServicioResponse;
import com.clinicaregional.clinica.entity.Servicio;
import com.clinicaregional.clinica.mapper.ServicioMapper;
import com.clinicaregional.clinica.repository.ServicioRepository;
import com.clinicaregional.clinica.service.impl.ServicioServiceImpl;
import com.clinicaregional.clinica.util.FiltroEstado;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServicioServiceImplTest {

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private ServicioMapper servicioMapper;

    @Mock
    private FiltroEstado filtroEstado;

    @InjectMocks
    private ServicioServiceImpl servicioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Agregar servicio exitosamente")
    void agregarServicio_debeRetornarServicioResponse() {
        // Arrange
        ServicioRequest request = new ServicioRequest("Cardiología", "Atiende problemas del corazón", "cardio.jpg");
        Servicio servicio = Servicio.builder()
                .nombre("Cardiología")
                .descripcion("Atiende problemas del corazón")
                .imagenUrl("cardio.jpg")
                .build();
        Servicio servicioGuardado = Servicio.builder()
                .id(1L)
                .nombre("Cardiología")
                .descripcion("Atiende problemas del corazón")
                .imagenUrl("cardio.jpg")
                .build();
        ServicioResponse response = new ServicioResponse(1L, "Cardiología", "Atiende problemas del corazón", "cardio.jpg");

        when(servicioRepository.existsByNombre("Cardiología")).thenReturn(false);
        when(servicioMapper.mapToServicio(request)).thenReturn(servicio);
        when(servicioRepository.save(servicio)).thenReturn(servicioGuardado);
        when(servicioMapper.mapToServicioResponse(servicioGuardado)).thenReturn(response);

        // Act
        ServicioResponse resultado = servicioService.agregarServicio(request);

        // Assert
        assertThat(resultado.getNombre()).isEqualTo("Cardiología");
        verify(servicioRepository).save(any());
    }

    @Test
    @DisplayName("Agregar servicio con nombre duplicado lanza excepción")
    void agregarServicio_conNombreDuplicado_debeLanzarExcepcion() {
        ServicioRequest request = new ServicioRequest("Cardiología", "desc", "img.jpg");
        when(servicioRepository.existsByNombre("Cardiología")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            servicioService.agregarServicio(request);
        });

        assertThat(exception.getMessage()).isEqualTo("Ya existe un servicio con el nombre ingresado");
    }

    @Test
    @DisplayName("Actualizar servicio correctamente")
    void actualizarServicio_existente_debeRetornarActualizado() {
        ServicioRequest request = new ServicioRequest("Pediatría", "Niños", "pediatria.jpg");
        Servicio existente = Servicio.builder().id(1L).nombre("Antiguo").descripcion("Antigua desc").estado(true).build();
        Servicio actualizado = Servicio.builder().id(1L).nombre("Pediatría").descripcion("Niños").imagenUrl("pediatria.jpg").estado(true).build();
        ServicioResponse response = new ServicioResponse(1L, "Pediatría", "Niños", "pediatria.jpg");

        when(servicioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(existente));
        when(servicioRepository.existsByNombre("Pediatría")).thenReturn(false);
        when(servicioRepository.save(any())).thenReturn(actualizado);
        when(servicioMapper.mapToServicioResponse(actualizado)).thenReturn(response);

        ServicioResponse result = servicioService.actualizarServicio(1L, request);

        assertThat(result.getNombre()).isEqualTo("Pediatría");
    }

    @Test
    @DisplayName("Actualizar servicio inexistente lanza excepción")
    void actualizarServicio_inexistente_debeLanzarExcepcion() {
        ServicioRequest request = new ServicioRequest("Nuevo", "desc", "img.jpg");
        when(servicioRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> servicioService.actualizarServicio(99L, request));
    }

    @Test
    @DisplayName("Actualizar servicio con nombre duplicado lanza excepción")
    void actualizarServicio_conNombreDuplicado_debeLanzarExcepcion() {
        ServicioRequest request = new ServicioRequest("Duplicado", "desc", "img.jpg");
        Servicio existente = Servicio.builder().id(1L).nombre("Original").estado(true).build();

        when(servicioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(existente));
        when(servicioRepository.existsByNombre("Duplicado")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> servicioService.actualizarServicio(1L, request));

        assertThat(exception.getMessage()).isEqualTo("Ya existe un servicio con el nombre ingresado");
    }

    @Test
    @DisplayName("Eliminar servicio correctamente")
    void eliminarServicio_existente_debeActualizarEstadoFalse() {
        Servicio servicio = Servicio.builder().id(1L).nombre("Pediatría").estado(true).build();
        when(servicioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(servicio));

        servicioService.eliminarServicio(1L);

        assertThat(servicio.getEstado()).isFalse();
        verify(servicioRepository).save(servicio);
    }

    @Test
    @DisplayName("Eliminar servicio inexistente lanza excepción")
    void eliminarServicio_inexistente_debeLanzarExcepcion() {
        when(servicioRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> servicioService.eliminarServicio(99L));
    }
}
