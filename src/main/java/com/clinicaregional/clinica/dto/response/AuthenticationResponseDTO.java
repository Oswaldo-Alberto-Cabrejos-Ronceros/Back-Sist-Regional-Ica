package com.clinicaregional.clinica.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) //para que solo se incluyan en las respuestas los not null
public class AuthenticationResponseDTO {
    private Long usuarioId;
    private String role;
    private String jwtToken;
    private String refreshToken;
    public AuthenticationResponseDTO(Long usuarioId, String role){
        this.usuarioId=usuarioId;
        this.role=role;
    }
}

