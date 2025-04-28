package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.UsuarioRequestDTO;
import com.clinicaregional.clinica.dto.AuthenticationResponseDTO;
import com.clinicaregional.clinica.dto.LoginRequestDTO;


public interface AuthenticationService {

    //para autenticar usuario
    public AuthenticationResponseDTO authenticateUser(LoginRequestDTO loginRequestDTO);

    //para refrescar token
    public String refreshToken(String refreshToken);

    public AuthenticationResponseDTO registerUser(UsuarioRequestDTO usuarioRequestDTO);
}
