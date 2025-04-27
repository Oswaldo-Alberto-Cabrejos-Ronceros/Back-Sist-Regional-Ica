package com.clinicaregional.clinica.usuarios.service;

import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.UsuarioRequestDTO;
import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.mapper.UsuarioMapper;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.repository.RolRepository;
import com.clinicaregional.clinica.service.impl.UsuarioServiceImpl;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UsuarioServiceImplTest {

        private UsuarioServiceImpl usuarioService;
        private UsuarioRepository usuarioRepository;
        private RolRepository rolRepository;
        private PasswordEncoder passwordEncoder;
        private UsuarioMapper usuarioMapper;
        private FiltroEstado filtroEstado;

        @BeforeEach
        void setUp() {
                usuarioRepository = mock(UsuarioRepository.class);
                rolRepository = mock(RolRepository.class);
                passwordEncoder = mock(PasswordEncoder.class);
                usuarioMapper = mock(UsuarioMapper.class);
                filtroEstado = mock(FiltroEstado.class);

                usuarioService = new UsuarioServiceImpl(
                                usuarioRepository,
                                rolRepository,
                                passwordEncoder,
                                usuarioMapper,
                                filtroEstado);

                lenient().doNothing().when(filtroEstado).activarFiltroEstado(true);
                lenient().when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        }

        @Test
        void guardar_usuarioNuevo_exitoso() {
                UsuarioRequestDTO request = new UsuarioRequestDTO(
                                "tester5461@gmail.com",
                                "password",
                                true,
                                new RolDTO(1L, "PACIENTE", "Paciente que usa el sistema"));

                Usuario usuario = new Usuario();
                usuario.setCorreo(request.getCorreo());
                usuario.setPassword("encodedPassword");

                when(usuarioRepository.existsByCorreoAndEstadoIsTrue(request.getCorreo())).thenReturn(false);
                when(usuarioMapper.mapFromUsuarioRequestDTOToUsuario(request)).thenReturn(usuario);
                when(rolRepository.findById(1L)).thenReturn(
                                Optional.of(new com.clinicaregional.clinica.entity.Rol(1L, "PACIENTE", "Paciente")));
                when(usuarioRepository.save(usuario)).thenReturn(usuario);
                when(usuarioMapper.mapToUsuarioDTO(usuario)).thenReturn(
                                new UsuarioDTO(1L, request.getCorreo(),
                                                new RolDTO(1L, "PACIENTE", "Paciente que usa el sistema")));

                UsuarioDTO result = usuarioService.guardar(request);

                assertThat(result).isNotNull();
                assertThat(result.getCorreo()).isEqualTo("tester5461@gmail.com");
        }

        @Test
        void guardar_usuarioExistente_lanzaExcepcion() {
                UsuarioRequestDTO request = new UsuarioRequestDTO(
                                "tester5461@gmail.com",
                                "password",
                                true,
                                new RolDTO(1L, "PACIENTE", "Paciente que usa el sistema"));

                when(usuarioRepository.existsByCorreoAndEstadoIsTrue(request.getCorreo())).thenReturn(true);

                assertThatThrownBy(() -> usuarioService.guardar(request))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessage("Ya existe un usuario con el correo ingresado");
        }

        @Test
        void obtenerPorId_existente_devuelveUsuario() {
                Usuario usuario = new Usuario();
                usuario.setId(1L);
                usuario.setCorreo("tester5461@gmail.com");

                when(usuarioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(usuario));
                when(usuarioMapper.mapToUsuarioDTO(usuario)).thenReturn(
                                new UsuarioDTO(1L, "tester5461@gmail.com",
                                                new RolDTO(1L, "PACIENTE", "Paciente que usa el sistema")));

                Optional<UsuarioDTO> result = usuarioService.obtenerPorId(1L);

                assertThat(result).isPresent();
                assertThat(result.get().getCorreo()).isEqualTo("tester5461@gmail.com");
        }

        @Test
        void eliminar_usuario_existente() {
                Usuario usuario = new Usuario();
                usuario.setId(1L);
                usuario.setCorreo("tester5461@gmail.com");

                when(usuarioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(usuario));

                usuarioService.eliminar(1L);

                verify(usuarioRepository).save(usuario);
        }

        @Test
        void guardar_usuarioSinRol_lanzaExcepcion() {
                UsuarioRequestDTO request = new UsuarioRequestDTO(
                                "correo@correo.com", "password", true, new RolDTO(99L, "NO_EXISTE", "Rol no existe"));

                when(usuarioRepository.existsByCorreoAndEstadoIsTrue(request.getCorreo())).thenReturn(false);
                when(usuarioMapper.mapFromUsuarioRequestDTOToUsuario(request)).thenReturn(new Usuario());
                when(rolRepository.findById(99L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> usuarioService.guardar(request))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessage("El rol especificado no existe");
        }

        @Test
        void actualizar_usuarioExistente_exitoso() {
                UsuarioRequestDTO request = new UsuarioRequestDTO(
                                "nuevo@correo.com", "nuevaPassword", true, new RolDTO(1L, "PACIENTE", "Paciente"));

                Usuario usuarioExistente = new Usuario();
                usuarioExistente.setId(1L);
                usuarioExistente.setCorreo("existente@correo.com");

                when(usuarioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(usuarioExistente));
                when(passwordEncoder.encode(request.getPassword())).thenReturn("passwordCodificada");
                when(rolRepository.findById(1L)).thenReturn(
                                Optional.of(new com.clinicaregional.clinica.entity.Rol(1L, "PACIENTE", "Paciente")));
                when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);
                when(usuarioMapper.mapToUsuarioDTO(usuarioExistente)).thenReturn(
                                new UsuarioDTO(1L, "existente@correo.com", new RolDTO(1L, "PACIENTE", "Paciente")));

                UsuarioDTO result = usuarioService.actualizar(1L, request);

                assertThat(result).isNotNull();
                assertThat(result.getCorreo()).isEqualTo("existente@correo.com");
        }

        @Test
        void actualizar_usuarioNoExistente_lanzaExcepcion() {
                UsuarioRequestDTO request = new UsuarioRequestDTO(
                                "correo@correo.com", "password", true, new RolDTO(1L, "PACIENTE", "Paciente"));

                when(usuarioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> usuarioService.actualizar(1L, request))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessageContaining("No existe un usuario con el id:");
        }

        @Test
        void actualizar_usuarioSinRolNuevo_lanzaExcepcion() {
                UsuarioRequestDTO request = new UsuarioRequestDTO(
                                "correo@correo.com", "password", true, new RolDTO(99L, "NO_EXISTE", "Rol no existe"));

                Usuario usuarioExistente = new Usuario();
                usuarioExistente.setId(1L);

                when(usuarioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(usuarioExistente));
                when(passwordEncoder.encode(request.getPassword())).thenReturn("passwordCodificada");
                when(rolRepository.findById(99L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> usuarioService.actualizar(1L, request))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessage("El rol especificado no existe");
        }

        @Test
        void eliminar_usuarioExistente_exitoso() {
                Usuario usuarioExistente = new Usuario();
                usuarioExistente.setId(1L);
                usuarioExistente.setEstado(true);

                when(usuarioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(usuarioExistente));

                usuarioService.eliminar(1L);

                assertThat(usuarioExistente.getEstado()).isFalse();
                verify(usuarioRepository, times(1)).save(usuarioExistente);
        }

        @Test
        void eliminar_usuarioNoExistente_lanzaExcepcion() {
                when(usuarioRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> usuarioService.eliminar(1L))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessageContaining("No existe un usuario con el id:");
        }

        @Test
        void listarUsuarios_exitoso() {
                Usuario usuario = new Usuario();
                usuario.setId(1L);
                usuario.setCorreo("correo@correo.com");

                when(usuarioRepository.findAll()).thenReturn(List.of(usuario));
                when(usuarioMapper.mapToUsuarioDTO(usuario)).thenReturn(
                                new UsuarioDTO(1L, "correo@correo.com", new RolDTO(1L, "PACIENTE", "Paciente")));

                List<UsuarioDTO> usuarios = usuarioService.listarUsuarios();

                assertThat(usuarios).isNotEmpty();
                assertThat(usuarios.get(0).getCorreo()).isEqualTo("correo@correo.com");
        }
}
