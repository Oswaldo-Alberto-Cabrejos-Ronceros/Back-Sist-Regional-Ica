package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.request.RegisterRequest;
import com.clinicaregional.clinica.dto.response.AuthenticationResponseDTO;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.exception.ValidationException;
import com.clinicaregional.clinica.dto.authentication.CompletePatientRegistrationRequest;
import com.clinicaregional.clinica.dto.authentication.VerifyCodeRequest;
import com.clinicaregional.clinica.dto.authentication.VerifyEmailRequest;
import com.clinicaregional.clinica.dto.authentication.VerifyPatientRequest;
import com.clinicaregional.clinica.dto.request.LoginRequestDTO;
import com.clinicaregional.clinica.service.AuthenticationService;
import com.clinicaregional.clinica.service.RegistracionPacienteService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Controller for Authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final RegistracionPacienteService registracionPacienteService;

    @Autowired
    public AuthenticationController(
            AuthenticationService authenticationService,
            RegistracionPacienteService registracionPacienteService // Inyectar nuevo servicio
    ) {
        this.authenticationService = authenticationService;
        this.registracionPacienteService = registracionPacienteService;
    }

    // funcion para agregar cookie a la respuesta
    private void addCokkie(HttpServletResponse response, String name, String value, int duration) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "None");
        cookie.setSecure(true); // en produccion ira en true al trabajar en https
        cookie.setPath("/");
        cookie.setMaxAge(duration);
        response.addCookie(cookie);
    }

    // funcion para eliminar una cookie
    private void deleteCokkie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "None");
        cookie.setSecure(true); // en produccion ira en true al trabajar en https
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
        try {
            AuthenticationResponseDTO authenticationResponseDTO = authenticationService
                    .authenticateUser(loginRequestDTO);
            addCokkie(response, "jwtToken", authenticationResponseDTO.getJwtToken(), 3600);
            addCokkie(response, "refreshToken", authenticationResponseDTO.getRefreshToken(), 432000);
            AuthenticationResponseDTO responseToSend = new AuthenticationResponseDTO(
                    authenticationResponseDTO.getUsuarioId(), authenticationResponseDTO.getRole());
            return ResponseEntity.ok(responseToSend);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Sin cookies"));
        }
        Optional<Cookie> refreshToken = Arrays.stream(cookies)
                .filter(c -> c.getName().equals("refreshToken"))
                .findFirst();
        if (refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Refresh token no encontrado"));
        }

        try {
            String newAccessToken = authenticationService.refreshToken(refreshToken.get().getValue());
            addCokkie(response, "jwtToken", newAccessToken, 3600);
            return ResponseEntity.ok(Map.of("Message", "Token refrescado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> registerPaciente(
            @RequestBody @Valid RegisterRequest registerRequest, HttpServletResponse response) {
        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.registerPaciente(registerRequest);
        // configuramos cookies httponly
        addCokkie(response, "jwtToken", authenticationResponseDTO.getJwtToken(), 3600);
        addCokkie(response, "refreshToken", authenticationResponseDTO.getRefreshToken(), 432000);
        AuthenticationResponseDTO responseToSend = new AuthenticationResponseDTO(
                authenticationResponseDTO.getUsuarioId(), authenticationResponseDTO.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseToSend);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        deleteCokkie(response, "jwtToken");
        deleteCokkie(response, "refreshToken");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of("Message", "Logout exitoso"));
    }

    // --- Nuevos endpoints para verificación ---
    @PostMapping("/verify-document")
    public ResponseEntity<?> verifyDocument(@RequestBody @Valid VerifyPatientRequest request) {
        boolean exists = registracionPacienteService.checkPatientExists(request);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @PostMapping("/send-code")
    public ResponseEntity<?> sendVerificationCode(
            @RequestBody @Valid VerifyEmailRequest request) {
        try {
            registracionPacienteService.sendVerificationEmail(request);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(
            @RequestBody @Valid VerifyCodeRequest request,
            @RequestParam String email) {
        boolean isValid = registracionPacienteService.verifyCode(request, email);
        return isValid
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Código inválido o expirado");
    }

    @PostMapping("/complete-registration")
    public ResponseEntity<?> completeRegistration(
            @RequestParam String documento,
            @RequestParam String email,
            @RequestBody @Valid CompletePatientRegistrationRequest request,
            HttpServletResponse response) {

        // Validar coincidencia de emails
        if (!email.equals(request.getEmail())) {
            return ResponseEntity.badRequest().body("El email no coincide");
        }
        AuthenticationResponseDTO authResponse = registracionPacienteService
                .completeExistingPatientRegistration(documento, request);

        addCokkie(response, "jwtToken", authResponse.getJwtToken(), 3600);
        addCokkie(response, "refreshToken", authResponse.getRefreshToken(), 432000);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthenticationResponseDTO(
                        authResponse.getUsuarioId(),
                        authResponse.getRole()));
    }
}
