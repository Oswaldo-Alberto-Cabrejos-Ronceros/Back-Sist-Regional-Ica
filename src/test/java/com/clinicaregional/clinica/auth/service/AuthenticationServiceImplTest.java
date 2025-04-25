package com.clinicaregional.clinica.auth.service;

import com.clinicaregional.clinica.dto.AuthenticationResponseDTO;
import com.clinicaregional.clinica.dto.LoginRequestDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.mapper.UsuarioMapper;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.service.impl.AuthenticationServiceImpl;
import com.clinicaregional.clinica.service.impl.UserDetailsServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {

    private AuthenticationServiceImpl authenticationService;

    private JwtUtil jwtUtil;
    private UsuarioService usuarioService;
    private UserDetailsServiceImpl userDetailsServiceImpl;
    private UsuarioMapper usuarioMapper;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        usuarioService = mock(UsuarioService.class);
        userDetailsServiceImpl = mock(UserDetailsServiceImpl.class);
        usuarioMapper = mock(UsuarioMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);

        authenticationService = new AuthenticationServiceImpl(
                jwtUtil,
                usuarioService,
                userDetailsServiceImpl,
                usuarioMapper,
                passwordEncoder);
    }

    @Test
    void authenticateUser_conCredencialesValidas_devuelveTokens() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@correo.com", "Password1");

        UserDetails userDetails = User.builder()
                .username("test@correo.com")
                .password("encodedPassword")
                .authorities("PACIENTE")
                .build();

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreo("test@correo.com");
        usuario.setPassword("encodedPassword");

        UsuarioDTO usuarioDTO = new UsuarioDTO(1L, "test@correo.com", true, null);

        when(userDetailsServiceImpl.loadUserByUsername("test@correo.com")).thenReturn(userDetails);
        when(passwordEncoder.matches("Password1", "encodedPassword")).thenReturn(true);
        when(usuarioService.obtenerPorCorreo("test@correo.com")).thenReturn(java.util.Optional.of(usuario));
        when(usuarioMapper.mapToUsuarioDTO(usuario)).thenReturn(usuarioDTO);
        when(jwtUtil.generateAccessToken(any(UsernamePasswordAuthenticationToken.class))).thenReturn("jwtToken");
        when(jwtUtil.generateRefreshToken(any(UsernamePasswordAuthenticationToken.class))).thenReturn("refreshToken");
        when(usuarioMapper.mapToAuthenticationResponseDTO(usuarioDTO, "jwtToken", "refreshToken"))
                .thenReturn(new AuthenticationResponseDTO(1L, "PACIENTE", "jwtToken", "refreshToken"));

        // Act
        AuthenticationResponseDTO response = authenticationService.authenticateUser(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getJwtToken()).isEqualTo("jwtToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(response.getUsuarioId()).isEqualTo(1L);
    }

    @Test
    void authenticateUser_conCredencialesInvalidas_lanzaExcepcion() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@correo.com", "wrongPassword");

        UserDetails userDetails = User.builder()
                .username("test@correo.com")
                .password("encodedPassword")
                .authorities("PACIENTE")
                .build();

        when(userDetailsServiceImpl.loadUserByUsername("test@correo.com")).thenReturn(userDetails);
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.authenticateUser(loginRequest))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class)
                .hasMessage("Credenciales incorrectas");
    }

    @Test
    void refreshToken_conTokenValido_devuelveNuevoAccessToken() {
        // Arrange
        String validRefreshToken = "refreshToken123";
        String email = "test@correo.com";
        UserDetails userDetails = User.builder()
                .username(email)
                .password("encodedPassword")
                .authorities("PACIENTE")
                .build();

        when(jwtUtil.validateToken(validRefreshToken)).thenReturn(true);
        when(jwtUtil.getEmailFromJwt(validRefreshToken)).thenReturn(email);
        when(userDetailsServiceImpl.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtUtil.generateAccessToken(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn("nuevoAccessToken123");

        // Act
        String nuevoToken = authenticationService.refreshToken(validRefreshToken);

        // Assert
        assertThat(nuevoToken).isEqualTo("nuevoAccessToken123");
    }

    @Test
    void refreshToken_conTokenInvalido_lanzaExcepcion() {
        // Arrange
        String invalidRefreshToken = "invalidToken";

        when(jwtUtil.validateToken(invalidRefreshToken)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.refreshToken(invalidRefreshToken))
                .isInstanceOf(io.jsonwebtoken.JwtException.class)
                .hasMessage("Error al validad token de refresco");
    }

}
