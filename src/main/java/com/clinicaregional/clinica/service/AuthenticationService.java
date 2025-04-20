package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.UsuarioRequest;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.models.AuthenticationResponse;
import com.clinicaregional.clinica.models.LoginRequest;
import org.springframework.security.core.Authentication;


public interface AuthenticationService {

    //para autenticar usuario
    public AuthenticationResponse authenticateUser(LoginRequest loginRequest);

    //para refrescar token
    public String refreshToken(String refreshToken);

    public AuthenticationResponse registerUser(UsuarioRequest usuarioRequest);
}
