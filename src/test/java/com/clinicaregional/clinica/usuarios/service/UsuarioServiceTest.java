package com.clinicaregional.clinica.usuarios.service;

import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.UsuarioRequestDTO;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.mapper.UsuarioMapper;
import com.clinicaregional.clinica.repository.AdministradorRepository;
import com.clinicaregional.clinica.repository.MedicoRepository;
import com.clinicaregional.clinica.repository.PacienteRepository;
import com.clinicaregional.clinica.repository.RecepcionistaRepository;
import com.clinicaregional.clinica.repository.RolRepository;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.service.impl.UsuarioServiceImpl;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private FiltroEstado filtroEstado;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Rol rol;
    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private UsuarioRequestDTO usuarioRequestDTO;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private AdministradorRepository administradorRepository;

    @Mock
    private RecepcionistaRepository recepcionistaRepository;

    @BeforeEach
    void setUp() {
        rol = new Rol(1L, "PACIENTE", "Paciente del sistema");
        usuario = new Usuario(1L, "tester5461@gmail.com", "Tester5461", rol);
        RolDTO rolDTO = new RolDTO(1L, "PACIENTE", "Paciente del sistema");
        usuarioDTO = new UsuarioDTO(1L, "tester5461@gmail.com", rolDTO);
        usuarioRequestDTO = new UsuarioRequestDTO("tester5461@gmail.com", "Tester5461", true, rolDTO);

        lenient().doNothing().when(filtroEstado).activarFiltroEstado(true);
        lenient().when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    }

    @Test
    @DisplayName("Test para guardar un usuario exitosamente")
    void guardarUsuario_exitoso() {
        when(usuarioRepository.existsByCorreoAndEstadoIsTrue(usuarioRequestDTO.getCorreo())).thenReturn(false);
        when(usuarioMapper.mapFromUsuarioRequestDTOToUsuario(usuarioRequestDTO)).thenReturn(usuario);
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.mapToUsuarioDTO(usuario)).thenReturn(usuarioDTO);

        UsuarioDTO result = usuarioService.guardar(usuarioRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getCorreo()).isEqualTo(usuario.getCorreo());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void listarUsuarios_exitoso() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));
        when(usuarioMapper.mapToUsuarioDTO(usuario)).thenReturn(usuarioDTO);

        List<UsuarioDTO> result = usuarioService.listarUsuarios();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCorreo()).isEqualTo("tester5461@gmail.com");
    }

    @Test
    void obtenerPorId_existente() {
        when(usuarioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.mapToUsuarioDTO(usuario)).thenReturn(usuarioDTO);

        Optional<UsuarioDTO> result = usuarioService.obtenerPorId(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getCorreo()).isEqualTo("tester5461@gmail.com");
    }

    @Test
    void obtenerPorId_noExistente() {
        when(usuarioRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        Optional<UsuarioDTO> result = usuarioService.obtenerPorId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void obtenerPorCorreo_existente() {
        when(usuarioRepository.findByCorreo("tester5461@gmail.com")).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.obtenerPorCorreo("tester5461@gmail.com");

        assertThat(result).isPresent();
        assertThat(result.get().getCorreo()).isEqualTo("tester5461@gmail.com");
    }

    @Test
    void obtenerPorCorreo_noExistente() {
        when(usuarioRepository.findByCorreo("noexiste@gmail.com")).thenReturn(Optional.empty());

        Optional<Usuario> result = usuarioService.obtenerPorCorreo("noexiste@gmail.com");

        assertThat(result).isEmpty();
    }

    @Test
    void obtenerPorRol_exitoso() {
        when(usuarioRepository.findByRol_Id(1L)).thenReturn(List.of(usuario));
        when(usuarioMapper.mapToUsuarioDTO(usuario)).thenReturn(usuarioDTO);

        List<UsuarioDTO> result = usuarioService.obtenerPorRol(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRol().getNombre()).isEqualTo("PACIENTE");
    }

    @Test
    void actualizarUsuario_exitoso() {
        when(usuarioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.mapToUsuarioDTO(usuario)).thenReturn(usuarioDTO);

        UsuarioDTO result = usuarioService.actualizar(1L, usuarioRequestDTO);

        assertThat(result.getCorreo()).isEqualTo("tester5461@gmail.com");
    }

    @Test
    void actualizarUsuario_noExistente_deberiaLanzarExcepcion() {
        when(usuarioRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.actualizar(99L, usuarioRequestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe un usuario con el id:");
    }

    @Test
    void eliminarUsuario_exitoso() {
        // Arrange
        when(usuarioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(usuario));

        // Simula que el usuario tiene el rol "PACIENTE"
        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setEstado(true);

        when(pacienteRepository.findByUsuario_Id(1L)).thenReturn(Optional.of(paciente));

        // Act
        usuarioService.eliminar(1L);

        // Assert
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(pacienteRepository, times(1)).save(any(Paciente.class));
    }

    @Test
    void eliminarUsuario_noExistente_deberiaLanzarExcepcion() {
        when(usuarioRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.eliminar(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe un usuario con el id:");
    }
}
