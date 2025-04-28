package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.request.RegisterAdministradorRequest;
import com.clinicaregional.clinica.dto.request.RegisterRequest;
import com.clinicaregional.clinica.dto.response.AuthenticationResponseDTO;
import com.clinicaregional.clinica.dto.request.LoginRequestDTO;
import com.clinicaregional.clinica.service.AuthenticationService;
import io.jsonwebtoken.JwtException;
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

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    //funcion para agregar cookie a la respuesta
    private void addCokkie(HttpServletResponse response,String name, String value){
        Cookie cookie = new Cookie(name,value);
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "Lax");
        cookie.setSecure(false); //en produccion ira en true al trabajar en https
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticateUser(loginRequestDTO);
        //configuramos cookies httponly
        addCokkie(response,"jwtToken",authenticationResponseDTO.getJwtToken());
        addCokkie(response,"refreshToken",authenticationResponseDTO.getRefreshToken());
        AuthenticationResponseDTO responseToSend = new AuthenticationResponseDTO(authenticationResponseDTO.getUsuarioId(), authenticationResponseDTO.getRole());
        return ResponseEntity.ok(responseToSend);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Cookie> refreshToken = Arrays.stream(cookies).filter(c -> c.getName().equals("refreshToken")).findFirst();
        if (refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String newAccessToken = authenticationService.refreshToken(refreshToken.get().getValue());
            //creamos nueva cookie
            addCokkie(response,"jwtToken",newAccessToken);
            return ResponseEntity.ok(Map.of("Message", "Token refrescado correctamente"));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(@RequestBody @Valid RegisterRequest registerRequest, HttpServletResponse response) {
        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.registerUser(registerRequest);
        //configuramos cookies httponly
        addCokkie(response,"jwtToken",authenticationResponseDTO.getJwtToken());
        addCokkie(response,"refreshToken",authenticationResponseDTO.getRefreshToken());
        AuthenticationResponseDTO responseToSend = new AuthenticationResponseDTO(authenticationResponseDTO.getUsuarioId(), authenticationResponseDTO.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseToSend);
    }

    //para registrar administrador

    @PostMapping("/administrador/register")
    public ResponseEntity<AuthenticationResponseDTO> administradorRegister(@RequestBody @Valid RegisterAdministradorRequest registerAdministradorRequest, HttpServletResponse response){
        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.registerAdministrador(registerAdministradorRequest);
        //configuramos cookies httponly
        addCokkie(response,"jwtToken",authenticationResponseDTO.getJwtToken());
        addCokkie(response,"refreshToken",authenticationResponseDTO.getRefreshToken());
        AuthenticationResponseDTO responseToSend = new AuthenticationResponseDTO(authenticationResponseDTO.getUsuarioId(), authenticationResponseDTO.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseToSend);
    }

}
