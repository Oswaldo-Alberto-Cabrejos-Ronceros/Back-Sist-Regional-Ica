package com.clinicaregional.clinica.recepcionista.service;

import com.clinicaregional.clinica.dto.request.RecepcionistaRequest;
import com.clinicaregional.clinica.dto.response.RecepcionistaResponse;
import com.clinicaregional.clinica.entity.Recepcionista;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.mapper.RecepcionistaMapper;
import com.clinicaregional.clinica.repository.RecepcionistaRepository;
import com.clinicaregional.clinica.repository.TipoDocumentoRepository;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.service.impl.RecepcionistaServiceImpl;
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

class RecepcionistaServiceImplTest {

    @Mock
    private RecepcionistaRepository recepcionistaRepository;
    @Mock
    private TipoDocumentoRepository tipoDocumentoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RecepcionistaMapper recepcionistaMapper;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private FiltroEstado filtroEstado;

    @InjectMocks
    private RecepcionistaServiceImpl recepcionistaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Listar recepcionistas activos")
    void listarRecepcionistas_debeRetornarLista() {
        // Arrange
        Recepcionista recepcionista = new Recepcionista();
        when(recepcionistaRepository.findAll()).thenReturn(List.of(recepcionista));
        when(recepcionistaMapper.toResponse(recepcionista)).thenReturn(RecepcionistaResponse.builder().build());

        // Act
        List<RecepcionistaResponse> resultado = recepcionistaService.listar();

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Obtener recepcionista por ID existente")
    void obtenerPorId_existente() {
        // Arrange
        Recepcionista recepcionista = new Recepcionista();
        recepcionista.setId(1L);
        when(recepcionistaRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(recepcionista));
        when(recepcionistaMapper.toResponse(recepcionista)).thenReturn(RecepcionistaResponse.builder().id(1L).build());

        // Act
        Optional<RecepcionistaResponse> resultado = recepcionistaService.obtenerPorId(1L);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Eliminar recepcionista debe poner estado en false")
    void eliminarRecepcionista_existente() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Recepcionista recepcionista = new Recepcionista();
        recepcionista.setId(1L);
        recepcionista.setUsuario(usuario);
        recepcionista.setEstado(true);
        when(recepcionistaRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(recepcionista));

        // Act
        recepcionistaService.eliminar(1L);

        // Assert
        assertThat(recepcionista.getEstado()).isFalse();
        verify(usuarioService).eliminar(1L);
        verify(recepcionistaRepository).save(recepcionista);
    }

    @Test
    @DisplayName("Guardar recepcionista con documento duplicado lanza excepción")
    void guardarRecepcionista_duplicado_lanzaExcepcion() {
        // Arrange
        RecepcionistaRequest recepcionistaRequest = new RecepcionistaRequest();
        recepcionistaRequest.setNumeroDocumento("12345678");
        when(recepcionistaRepository.existsByNumeroDocumento("12345678")).thenReturn(true);

        // Act + Assert
        assertThrows(RuntimeException.class, () -> recepcionistaService.guardar(recepcionistaRequest));
    }

    @Test
    @DisplayName("Actualizar recepcionista inexistente lanza excepción")
    void actualizarRecepcionista_inexistente_lanzaExcepcion() {
        // Arrange
        when(recepcionistaRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());
        RecepcionistaRequest recepcionistaRequest = new RecepcionistaRequest();

        // Act + Assert
        assertThrows(RuntimeException.class, () -> recepcionistaService.actualizar(99L, recepcionistaRequest));
    }

    @Test
    @DisplayName("Eliminar recepcionista inexistente debe lanzar excepción")
    void eliminarRecepcionista_inexistente() {
        // Arrange
        when(recepcionistaRepository.findByIdAndEstadoIsTrue(123L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class, () -> recepcionistaService.eliminar(123L));
    }

}
