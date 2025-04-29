package com.clinicaregional.clinica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoDocumentoDTO {
    private Long id;

    @NotBlank(message = "Nombre es obligatorio")
    @Size(max=32, message = "El nombre debe tener menos de 32 caracteres")
    private String nombre;

    @NotBlank(message = "Descripcion es obligatorio")
    @Size(max=255, message = "La descripcion debe tener menos de 255 caracteres")
    private String descripcion;
}
