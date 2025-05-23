package com.clinicaregional.clinica.medico.service;

import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.MedicoRequestDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponseDTO;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.enums.TipoContrato;
import com.clinicaregional.clinica.enums.TipoMedico;
import com.clinicaregional.clinica.mapper.MedicoMapper;
import com.clinicaregional.clinica.repository.MedicoRepository;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.service.UsuarioService;
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
import static org.mockito.Mockito.*;

class MedicoServiceImplTest {

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private MedicoMapper medicoMapper;

    @Mock
    private FiltroEstado filtroEstado;

    @InjectMocks
    private MedicoServiceImpl medicoService;

    private Medico medico;
    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private MedicoRequestDTO medicoRequestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreo("medico@gmail.com");

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setCorreo("medico@gmail.com");

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
                LocalDateTime.now(), TipoContrato.FIJO, TipoMedico.ESPECIALISTA, 1L,
                "juanPerez@gmail.com", "Password123"
        );
    }

    @Test
    @DisplayName("Listar todos los médicos activos")
    void listarMedicos_activos() {
        when(medicoRepository.findAll()).thenReturn(List.of(medico));
        when(medicoMapper.mapToMedicoResponseDTO(medico)).thenReturn(new MedicoResponseDTO(
                1L, "Juan", "Perez", "12345678901", "987654321",
                "999999999", "Calle Salud 123", "Cardiologo especializado", "imagen.jpg",
                LocalDateTime.now(), TipoContrato.FIJO, TipoMedico.ESPECIALISTA, usuario.getId()
        ));

        List<MedicoResponseDTO> resultado = medicoService.obtenerMedicos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombres()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("Guardar nuevo médico exitosamente")
    void guardarMedico_exitoso() {
        when(medicoRepository.existsByNumeroColegiatura(any())).thenReturn(false);
        when(medicoRepository.existsByNumeroRNE(any())).thenReturn(false);
        when(usuarioRepository.existsByCorreo(any())).thenReturn(false);
        when(usuarioService.guardar(any())).thenReturn(usuarioDTO);
        when(medicoRepository.save(any())).thenReturn(medico);
        when(medicoMapper.mapToMedicoResponseDTO(any())).thenReturn(new MedicoResponseDTO(
                1L, "Juan", "Perez", "12345678901", "987654321",
                "999999999", "Calle Salud 123", "Cardiologo especializado", "imagen.jpg",
                LocalDateTime.now(), TipoContrato.FIJO, TipoMedico.ESPECIALISTA, usuario.getId()
        ));

        MedicoResponseDTO resultado = medicoService.guardarMedico(medicoRequestDTO);

        assertThat(resultado.getNombres()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("Guardar médico con colegiatura duplicada debe lanzar excepción")
    void guardarMedico_colegiaturaDuplicada() {
        when(medicoRepository.existsByNumeroColegiatura(any())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> medicoService.guardarMedico(medicoRequestDTO));
    }

    @Test
    @DisplayName("Actualizar médico existente exitosamente")
    void actualizarMedico_existente() {
        when(medicoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(medico));
        when(usuarioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(usuario));
        when(medicoRepository.existsByNumeroColegiatura(any())).thenReturn(false);
        when(medicoRepository.existsByNumeroRNE(any())).thenReturn(false);
        when(medicoRepository.existsByUsuario(usuario)).thenReturn(false);
        when(medicoRepository.save(any())).thenReturn(medico);
        when(medicoMapper.mapToMedicoResponseDTO(any())).thenReturn(new MedicoResponseDTO(
                1L, "Juan", "Perez", "12345678901", "987654321",
                "999999999", "Calle Salud 123", "Cardiologo especializado", "imagen.jpg",
                LocalDateTime.now(), TipoContrato.FIJO, TipoMedico.ESPECIALISTA, usuario.getId()
        ));

        MedicoResponseDTO resultado = medicoService.actualizarMedico(1L, medicoRequestDTO);

        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Actualizar médico inexistente debe lanzar excepción")
    void actualizarMedico_inexistente() {
        when(medicoRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> medicoService.actualizarMedico(99L, medicoRequestDTO));
    }

    @Test
    @DisplayName("Eliminar médico existente exitosamente")
    void eliminarMedico_existente() {
        when(medicoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(medico));
        when(usuarioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(usuario));

        medicoService.eliminarMedico(1L);

        assertThat(medico.getEstado()).isFalse();
        verify(usuarioRepository).save(usuario);
        verify(medicoRepository).save(medico);
    }

    @Test
    @DisplayName("Eliminar médico inexistente debe lanzar excepción")
    void eliminarMedico_inexistente() {
        when(medicoRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> medicoService.eliminarMedico(99L));
    }
}
