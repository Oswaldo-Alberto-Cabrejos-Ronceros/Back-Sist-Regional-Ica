package com.clinicaregional.clinica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EspecialidadRequest {
    @NotBlank(message = "Nombre es obligatorio")
    @Size(max = 64, message = "Nombre debe ser menor a 64")
    private String nombre;

    @NotBlank(message = "Descripción es obligatoria")
    private String descripcion;

    private String imagen; //poner imagen predeterminanda

}

