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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

        when(usuarioRepository.existsByCorreo(request.getCorreo())).thenReturn(false);
        when(usuarioMapper.mapFromUsuarioRequestDTOToUsuario(request)).thenReturn(usuario);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(rolRepository.findById(1L))
                .thenReturn(Optional.of(new com.clinicaregional.clinica.entity.Rol(1L, "PACIENTE", "Paciente")));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.mapToUsuarioDTO(usuario)).thenReturn(
                new UsuarioDTO(1L, request.getCorreo(), new RolDTO(1L, "PACIENTE", "Paciente que usa el sistema")));

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

        when(usuarioRepository.existsByCorreo(request.getCorreo())).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.guardar(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Ya existe un usuario con el correo ingresado");
    }

    @Test
    void obtenerPorId_existente_devuelveUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreo("tester5461@gmail.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.mapToUsuarioDTO(usuario)).thenReturn(
                new UsuarioDTO(1L, "tester5461@gmail.com", new RolDTO(1L, "PACIENTE", "Paciente que usa el sistema")));

        Optional<UsuarioDTO> result = usuarioService.obtenerPorId(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getCorreo()).isEqualTo("tester5461@gmail.com");
    }

    @Test
    void eliminar_usuario_existente() {
        Long id = 1L;

        usuarioService.eliminar(id);

        verify(usuarioRepository).deleteById(id);
    }
}
