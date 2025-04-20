package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.UsuarioRequestDTO;
import com.clinicaregional.clinica.dto.AuthenticationResponseDTO;
import com.clinicaregional.clinica.dto.LoginRequestDTO;
import com.clinicaregional.clinica.service.AuthenticationService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticateUser(loginRequestDTO);
        //configuramos cookies httponly
        Cookie accessToken = new Cookie("jwtToken", authenticationResponseDTO.getJwtToken());
        accessToken.setHttpOnly(true);
        accessToken.setAttribute("SameSite", "Lax");
        accessToken.setSecure(false); //en produccion ira en true al trabajar en https
        accessToken.setPath("/");
        response.addCookie(accessToken);
        Cookie refreshToken = new Cookie("refreshToken", authenticationResponseDTO.getRefreshToken());
        refreshToken.setHttpOnly(true);
        refreshToken.setAttribute("SameSite", "Lax");
        refreshToken.setSecure(false);
        refreshToken.setPath("/");
        response.addCookie(refreshToken);
        AuthenticationResponseDTO responseToSend = new AuthenticationResponseDTO(authenticationResponseDTO.getUsuarioId(), authenticationResponseDTO.getName(), authenticationResponseDTO.getRole());
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
            String newRefreshToken = authenticationService.refreshToken(refreshToken.get().getValue());
            //creamos nueva cookie
            Cookie refreshTokenCookie = new Cookie("refreshToken", newRefreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setAttribute("SameSite", "Lax");
            refreshTokenCookie.setSecure(false);
            refreshTokenCookie.setPath("/");
            response.addCookie(refreshTokenCookie);
            return ResponseEntity.ok(Map.of("Message", "Token refrescado correctamente"));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(@RequestBody UsuarioRequestDTO UsuarioRequestDTO, HttpServletResponse response) {
        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.registerUser(UsuarioRequestDTO);
        //configuramos cookies httponly
        Cookie accessToken = new Cookie("jwtToken", authenticationResponseDTO.getJwtToken());
        accessToken.setHttpOnly(true);
        accessToken.setAttribute("SameSite", "Lax");
        accessToken.setSecure(false); //en produccion ira en true al trabajar en https
        accessToken.setPath("/");
        response.addCookie(accessToken);
        Cookie refreshToken = new Cookie("refreshToken", authenticationResponseDTO.getRefreshToken());
        refreshToken.setHttpOnly(true);
        refreshToken.setAttribute("SameSite", "Lax");
        refreshToken.setSecure(false);
        refreshToken.setPath("/");
        response.addCookie(refreshToken);
        AuthenticationResponseDTO responseToSend = new AuthenticationResponseDTO(authenticationResponseDTO.getUsuarioId(), authenticationResponseDTO.getName(), authenticationResponseDTO.getRole());
        return ResponseEntity.ok(responseToSend);
    }

}
