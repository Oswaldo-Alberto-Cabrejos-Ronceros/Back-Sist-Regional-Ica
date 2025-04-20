package com.clinicaregional.clinica.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class UsuarioDTO {

    private Long id;
    private String nombre;
    private String correo;
    private boolean estado;
    private RolDTO rol;
    
}
