package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.RegisterAdministradorRequest;
import com.clinicaregional.clinica.dto.request.RegisterRequest;
import com.clinicaregional.clinica.dto.response.AuthenticationResponseDTO;
import com.clinicaregional.clinica.dto.request.LoginRequestDTO;


public interface AuthenticationService {

    //para autenticar usuario
    public AuthenticationResponseDTO authenticateUser(LoginRequestDTO loginRequestDTO);

    //para refrescar token
    public String refreshToken(String refreshToken);

    public AuthenticationResponseDTO registerPaciente(RegisterRequest registerRequest);

    AuthenticationResponseDTO registerAdministrador(RegisterAdministradorRequest registerAdministradorRequest);
}
