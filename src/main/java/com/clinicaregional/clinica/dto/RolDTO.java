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
public class RolDTO {

    private Long id;
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 32, message = "El nombre debe tener menos de 32 caracteres")
    private String nombre;
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max=255, message = "La descripción debe tener menos de 255 caracteres")
    private String descripcion;
    public RolDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}
