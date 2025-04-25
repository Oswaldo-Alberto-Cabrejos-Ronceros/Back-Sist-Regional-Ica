package com.clinicaregional.clinica.auth.service;

import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    private UserDetailsServiceImpl userDetailsService;
    private UsuarioService usuarioService; // mock

    @BeforeEach
    void setUp() {
        usuarioService = mock(UsuarioService.class);
        userDetailsService = new UserDetailsServiceImpl(usuarioService);
    }

    @Test
    void loadUserByUsername_usuarioExiste_devuelveUserDetails() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setCorreo("tester5461@gmail.com");
        usuario.setPassword("encodedPassword");
        usuario.setRol(new com.clinicaregional.clinica.entity.Rol());
        usuario.getRol().setNombre("PACIENTE");

        when(usuarioService.obtenerPorCorreo("tester5461@gmail.com")).thenReturn(Optional.of(usuario));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("tester5461@gmail.com");

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("tester5461@gmail.com");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
        assertThat(userDetails.getAuthorities()).extracting("authority").containsExactly("PACIENTE");
    }

    @Test
    void loadUserByUsername_usuarioNoExiste_lanzaException() {
        // Arrange
        when(usuarioService.obtenerPorCorreo("noexiste@gmail.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("noexiste@gmail.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuario no encontrado");
    }
}
    