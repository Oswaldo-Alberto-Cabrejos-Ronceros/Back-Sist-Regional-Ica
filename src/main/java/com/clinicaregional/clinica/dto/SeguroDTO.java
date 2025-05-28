package com.clinicaregional.clinica.dto;

import com.clinicaregional.clinica.enums.EstadoSeguro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeguroDTO {
    private Long id;

    @NotBlank(message = "Nombre es obligatorio")
    @Size(max = 64, message = "Nombre debe tener menos de 64 caracteres")
    private String nombre;

    @NotBlank(message = "Descripcion es obligatorio")
    @Size (max = 255, message = "Decripcion debe tener menos de 255 caracteres")
    private String descripcion;

    private String imagenUrl; //despues agregar imagen predeterminada

    @NotNull(message = "EstadoSeguro es obligatorio")
    private EstadoSeguro estadoSeguro;
}