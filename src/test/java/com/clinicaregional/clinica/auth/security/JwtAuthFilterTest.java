package com.clinicaregional.clinica.auth.security;

import com.clinicaregional.clinica.security.JwtAuthFilter;
import com.clinicaregional.clinica.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    private JwtAuthFilter jwtAuthFilter;
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        jwtAuthFilter = new JwtAuthFilter(jwtUtil);

        // Importante: limpiar el contexto de autenticación antes de cada test
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_tokenValido_autenticaUsuario() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("jwtToken", "validToken"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        when(jwtUtil.validateToken("validToken")).thenReturn(true);
        when(jwtUtil.getEmailFromJwt("validToken")).thenReturn("tester5461@gmail.com");
        when(jwtUtil.getAuthoritiesFromJwt("validToken")).thenReturn(List.of(new SimpleGrantedAuthority("PACIENTE")));

        // Act
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Assert
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo("tester5461@gmail.com");
        assertThat(auth.getAuthorities()).extracting("authority").containsExactly("PACIENTE");

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_sinCookies_noAutentica() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest(); // sin cookies
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // Act
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Assert
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull(); // no debe autenticar

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_tokenInvalido_noAutentica() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("jwtToken", "invalidToken"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        when(jwtUtil.validateToken("invalidToken")).thenReturn(false);

        // Act
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Assert
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull(); // no debe autenticar

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotFilter_rutasPermitidas_noAplicaFiltro() throws Exception {
        // Act
        var loginRequest = new MockHttpServletRequest("POST", "/api/auth/login");
        var registerRequest = new MockHttpServletRequest("POST", "/api/auth/register");
        var refreshRequest = new MockHttpServletRequest("POST", "/api/auth/refresh");
        var otraRequest = new MockHttpServletRequest("POST", "/api/otro-endpoint");

        // Assert
        assertThat(jwtAuthFilter.shouldNotFilter(loginRequest)).isTrue();
        assertThat(jwtAuthFilter.shouldNotFilter(registerRequest)).isTrue();
        assertThat(jwtAuthFilter.shouldNotFilter(refreshRequest)).isTrue();
        assertThat(jwtAuthFilter.shouldNotFilter(otraRequest)).isFalse(); // no está permitido
    }
}
