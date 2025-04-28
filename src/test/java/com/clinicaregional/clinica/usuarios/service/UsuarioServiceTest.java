package com.clinicaregional.clinica.usuarios.service;

import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.UsuarioRequestDTO;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.mapper.UsuarioMapper;
import com.clinicaregional.clinica.repository.RolRepository;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.service.impl.UsuarioServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Rol rol;
    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private UsuarioRequestDTO usuarioRequestDTO;

    @BeforeEach
    void setUp() {
        rol = new Rol(1L, "PACIENTE", "Paciente del sistema");
        usuario = new Usuario(1L, "tester5461@gmail.com", "Tester5461", true, rol);
        RolDTO rolDTO = new RolDTO(1L, "PACIENTE", "Paciente del sistema");
        usuarioDTO = new UsuarioDTO(1L, "tester5461@gmail.com", true, rolDTO);
        usuarioRequestDTO = new UsuarioRequestDTO("tester5461@gmail.com", "Tester5461", true, rolDTO);
    }

    @Test
    void guardarUsuario_exitoso() {
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(usuarioMapper.mapFromUsuarioRequestDTOToUsuario(usuarioRequestDTO)).thenReturn(usuario);
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
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.mapToUsuarioDTO(usuario)).thenReturn(usuarioDTO);

        Optional<UsuarioDTO> result = usuarioService.obtenerPorId(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getCorreo()).isEqualTo("tester5461@gmail.com");
    }

    @Test
    void obtenerPorId_noExistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

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
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.mapToUsuarioDTO(usuario)).thenReturn(usuarioDTO);

        UsuarioDTO result = usuarioService.actualizar(1L, usuarioRequestDTO);

        assertThat(result.getCorreo()).isEqualTo("tester5461@gmail.com");
    }

    @Test
    void actualizarUsuario_noExistente_deberiaLanzarExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.actualizar(99L, usuarioRequestDTO))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void eliminarUsuario_exitoso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.eliminar(1L);

        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarUsuario_noExistente_deberiaLanzarExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.eliminar(99L))
                .isInstanceOf(RuntimeException.class);
    }
}
