package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.RegisterRequest;
import com.clinicaregional.clinica.dto.response.AuthenticationResponseDTO;
import com.clinicaregional.clinica.dto.request.LoginRequestDTO;

public interface AuthenticationService {
    //para autenticar usuario
    AuthenticationResponseDTO authenticateUser(LoginRequestDTO loginRequestDTO);

    //para refrescar token
    String refreshToken(String refreshToken);

    AuthenticationResponseDTO registerPaciente(RegisterRequest registerRequest);

}
