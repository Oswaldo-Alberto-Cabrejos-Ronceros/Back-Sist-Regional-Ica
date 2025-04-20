package com.clinicaregional.clinica.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RolDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    public RolDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}
