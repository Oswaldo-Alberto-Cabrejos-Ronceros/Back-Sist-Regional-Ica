package com.clinicaregional.clinica.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsuarioRequestDTO {
    private String nombre;
    private String correo;
    private String password;
    private boolean estado;
    private RolDTO rol; 

}
