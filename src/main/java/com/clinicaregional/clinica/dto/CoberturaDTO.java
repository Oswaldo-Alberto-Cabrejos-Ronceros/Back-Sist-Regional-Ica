package com.clinicaregional.clinica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoberturaDTO {

    private Long id;

    @NotBlank(message = "Nombre es obligatorio")
    @Size(max = 64, message = "Nombre debe tener menos de 64 caracteres")
    private String nombre;

    @NotBlank(message = "Descripcion es obligatorio")
    @Size(max = 255, message = "Nombre debe tener menos de 255 caracteres")
    private String descripcion;

}
