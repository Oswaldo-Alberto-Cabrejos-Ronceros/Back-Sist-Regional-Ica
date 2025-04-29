package com.clinicaregional.clinica.medico.service;

import com.clinicaregional.clinica.dto.request.MedicoRequestDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponseDTO;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.enums.TipoContrato;
import com.clinicaregional.clinica.enums.TipoMedico;
import com.clinicaregional.clinica.mapper.MedicoMapper;
import com.clinicaregional.clinica.repository.MedicoRepository;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.service.impl.MedicoServiceImpl;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MedicoServiceTest {

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MedicoMapper medicoMapper;

    @Mock
    private FiltroEstado filtroEstado;

    @InjectMocks
    private MedicoServiceImpl medicoService;

    private Usuario usuario;
    private Medico medico;
    private MedicoRequestDTO medicoRequestDTO;
    private MedicoResponseDTO medicoResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreo("medico@gmail.com");
        usuario.setPassword("password");
        usuario.setEstado(true);

        medico = Medico.builder()
                .id(1L)
                .nombres("Juan")
                .apellidos("Perez")
                .numeroColegiatura("12345678901")
                .numeroRNE("987654321")
                .telefono("999999999")
                .direccion("Calle Salud 123")
                .descripcion("Cardiologo especializado")
                .imagen("imagen.jpg")
                .fechaContratacion(LocalDateTime.now())
                .tipoContrato(TipoContrato.FIJO)
                .tipoMedico(TipoMedico.ESPECIALISTA)
                .usuario(usuario)
                .estado(true)
                .build();

        medicoRequestDTO = new MedicoRequestDTO(
                "Juan", "Perez", "12345678901", "987654321",
                "999999999", "Calle Salud 123", "Cardiologo especializado", "imagen.jpg",
                LocalDateTime.now(), TipoContrato.FIJO, TipoMedico.ESPECIALISTA, 1L
        );

        medicoResponseDTO = new MedicoResponseDTO(
                1L, "Juan", "Perez", "12345678901", "987654321",
                "999999999", "Calle Salud 123", "Cardiologo especializado", "imagen.jpg",
                LocalDateTime.now(), TipoContrato.FIJO, TipoMedico.ESPECIALISTA
        );
    }

    @Test
    @DisplayName("Listar todos los médicos activos")
    void obtenerMedicos_debeRetornarLista() {
        // Arrange
        when(medicoRepository.findAll()).thenReturn(List.of(medico));
        when(medicoMapper.mapToMedicoResponseDTO(any(Medico.class))).thenReturn(medicoResponseDTO);

        // Act
        List<MedicoResponseDTO> resultado = medicoService.obtenerMedicos();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombres()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("Guardar nuevo médico correctamente")
    void guardarMedico_nuevo_debeRetornarDTO() {
        // Arrange
        when(medicoRepository.existsByNumeroColegiatura(any())).thenReturn(false);
        when(medicoRepository.existsByNumeroRNE(any())).thenReturn(false);
        when(usuarioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(usuario));
        when(medicoRepository.existsByUsuario(any())).thenReturn(false);
        when(medicoMapper.mapToMedico(any(MedicoRequestDTO.class), any(Usuario.class))).thenReturn(medico);
        when(medicoRepository.save(any(Medico.class))).thenReturn(medico);
        when(medicoMapper.mapToMedicoResponseDTO(any(Medico.class))).thenReturn(medicoResponseDTO);

        // Act
        MedicoResponseDTO resultado = medicoService.guardarMedico(medicoRequestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombres()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("Guardar médico existente por colegiatura debe lanzar excepción")
    void guardarMedico_existenteColegialura_debeLanzarExcepcion() {
        // Arrange
        when(medicoRepository.existsByNumeroColegiatura(any())).thenReturn(true);

        // Act + Assert
        assertThrows(RuntimeException.class, () -> medicoService.guardarMedico(medicoRequestDTO));
    }

    @Test
    @DisplayName("Actualizar médico existente correctamente")
    void actualizarMedico_existente_debeActualizar() {
        // Arrange
        when(medicoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(medico));
        when(usuarioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(usuario));
        when(medicoRepository.existsByNumeroColegiatura(any())).thenReturn(false);
        when(medicoRepository.existsByNumeroRNE(any())).thenReturn(false);
        when(medicoRepository.existsByUsuario(any())).thenReturn(false);
        when(medicoRepository.save(any())).thenReturn(medico);
        when(medicoMapper.mapToMedicoResponseDTO(any())).thenReturn(medicoResponseDTO);

        // Act
        MedicoResponseDTO resultado = medicoService.actualizarMedico(1L, medicoRequestDTO);

        // Assert
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Eliminar médico debe cambiar su estado a false")
    void eliminarMedico_existente_debeActualizarEstado() {
        // Arrange
        when(medicoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(medico));

        // Act
        medicoService.eliminarMedico(1L);

        // Assert
        assertThat(medico.getEstado()).isFalse();
        verify(medicoRepository, times(1)).save(medico);
    }
}
