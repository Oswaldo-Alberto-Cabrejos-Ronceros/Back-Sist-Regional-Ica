
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthenticationController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
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
        AuthenticationResponseDTO response = new AuthenticationResponseDTO(1L, "PACIENTE", "accessToken", "refreshToken");

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
}
