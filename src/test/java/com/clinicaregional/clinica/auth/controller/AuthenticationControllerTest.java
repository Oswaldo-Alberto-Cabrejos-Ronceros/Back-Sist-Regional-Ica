
package com.clinicaregional.clinica.auth.controller;

import com.clinicaregional.clinica.controller.AuthenticationController;
import com.clinicaregional.clinica.dto.AuthenticationResponseDTO;
import com.clinicaregional.clinica.dto.LoginRequestDTO;
import com.clinicaregional.clinica.dto.UsuarioRequestDTO;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = AuthenticationController.class, excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
})
class AuthenticationControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AuthenticationService authenticationService;

        @MockitoBean
        private JwtUtil jwtUtil;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void loginExitoso_conCredencialesValidas_devuelveJwt() throws Exception {
                LoginRequestDTO request = new LoginRequestDTO("testerDiego@gmail.com", "123456");
                AuthenticationResponseDTO response = new AuthenticationResponseDTO(1L, "PACIENTE", "accessToken",
                                "refreshToken");

                when(authenticationService.authenticateUser(any())).thenReturn(response);

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(cookie().exists("jwtToken"))
                                .andExpect(cookie().exists("refreshToken"))
                                .andExpect(jsonPath("$.usuarioId").value(1L))
                                .andExpect(jsonPath("$.role").value("PACIENTE"));
        }

        @Test
        void loginFallido_conCredencialesInvalidas_devuelve401() throws Exception {
                LoginRequestDTO request = new LoginRequestDTO("testerDiego@gmail.com", "123456");

                when(authenticationService.authenticateUser(any()))
                                .thenThrow(new RuntimeException("Credenciales incorrectas"));

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.error").value("Credenciales incorrectas"));
        }

        @Test
        void refreshToken_exitoso_conCookieValida() throws Exception {
                Cookie refreshCookie = new Cookie("refreshToken", "validRefreshToken");
                when(authenticationService.refreshToken("validRefreshToken"))
                                .thenReturn("nuevoAccessToken");

                mockMvc.perform(post("/api/auth/refresh")
                                .cookie(refreshCookie))
                                .andExpect(status().isOk())
                                .andExpect(cookie().exists("jwtToken"))
                                .andExpect(jsonPath("$.Message").value("Token refrescado correctamente"));
        }

        @Test
        void refreshToken_fallido_sinCookies_devuelve401() throws Exception {
                mockMvc.perform(post("/api/auth/refresh"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void refreshToken_fallido_conTokenInvalido_devuelve401() throws Exception {
                Cookie refreshCookie = new Cookie("refreshToken", "invalidToken");

                when(authenticationService.refreshToken("invalidToken"))
                                .thenThrow(new RuntimeException("Token inválido"));

                mockMvc.perform(post("/api/auth/refresh")
                                .cookie(refreshCookie))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.error").value("Token inválido"));
        }

        @Test
        void registroUsuario_nuevoUsuario_devuelveJWTYCookies() throws Exception {
                UsuarioRequestDTO request = new UsuarioRequestDTO("diegoTester@gmail.com", "Tester5461", true, null);
                AuthenticationResponseDTO response = new AuthenticationResponseDTO(10L, "PACIENTE", "jwt123",
                                "refresh123");

                when(authenticationService.registerUser(any())).thenReturn(response);

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(cookie().exists("jwtToken"))
                                .andExpect(cookie().exists("refreshToken"))
                                .andExpect(jsonPath("$.usuarioId").value(10L))
                                .andExpect(jsonPath("$.role").value("PACIENTE"));
        }

        @Test
        void loginFallido_conBodyVacio_devuelve400() throws Exception {
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")) // body vacío
                                .andExpect(status().isBadRequest());
        }

        @Test
        void registroFallido_conDatosInvalidos_devuelve400() throws Exception {
                UsuarioRequestDTO requestInvalido = new UsuarioRequestDTO("", "", true, null);

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestInvalido)))
                                .andExpect(status().isBadRequest());
        }
}
