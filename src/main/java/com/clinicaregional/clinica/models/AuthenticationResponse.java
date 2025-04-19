package com.clinicaregional.clinica.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private Long usuarioId;
    private String name;
    private String role;
    private String jwtToken;
    private String refreshToken;
    public AuthenticationResponse(Long usuarioId, String name, String role){
        this.usuarioId=usuarioId;
        this.name=name;
        this.role=role;
    }

}

