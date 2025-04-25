package com.clinicaregional.clinica.auth.security;

import com.clinicaregional.clinica.security.JwtUtil;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Simulamos el secretKey manualmente
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "miSuperClaveSecreta5461miSuperClaveSecreta5461");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 3600000);
        ReflectionTestUtils.setField(jwtUtil, "refreshExpirationMs", 86400000);
        jwtUtil.init();
    }

    @Test
    void generateAccessToken_y_validateToken_exitoso() {
        // Arrange
        UserDetails userDetails = new User("tester5461@gmail.com", "password",
                List.of(new SimpleGrantedAuthority("PACIENTE")));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(), null, userDetails.getAuthorities());

        // Act
        String token = jwtUtil.generateAccessToken(authentication);

        // Assert
        assertThat(token).isNotNull();
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_conTokenInvalido_devuelveFalse() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThat(jwtUtil.validateToken(invalidToken)).isFalse();
    }

    @Test
    void getEmailFromJwt_extraeEmailCorrectamente() {
        // Arrange
        UserDetails userDetails = new User("tester5461@gmail.com", "password",
                List.of(new SimpleGrantedAuthority("PACIENTE")));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(), null, userDetails.getAuthorities());

        String token = jwtUtil.generateAccessToken(authentication);

        // Act
        String emailExtraido = jwtUtil.getEmailFromJwt(token);

        // Assert
        assertThat(emailExtraido).isEqualTo("tester5461@gmail.com");
    }

    @Test
    void getEmailFromJwt_conTokenInvalido_lanzaExcepcion() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThatThrownBy(() -> jwtUtil.getEmailFromJwt(invalidToken))
                .isInstanceOf(io.jsonwebtoken.JwtException.class)
                .hasMessageContaining("Invalid JWT token");
    }
}
