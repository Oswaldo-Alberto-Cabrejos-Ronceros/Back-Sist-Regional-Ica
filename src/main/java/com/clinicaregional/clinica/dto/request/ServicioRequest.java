package com.clinicaregional.clinica.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ServicioRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(min = 1, max = 255, message = "La descripción debe tener entre 1 y 255 caracteres")
    private String descripcion;

    private String imagenUrl;

    @NotNull(message = "Precio no puede estar vacio")
    @DecimalMin(value = "0.0", message = "El precio debe ser mayor o igual a 0.0")
    private Float price;

    @NotNull(message = "Especialidad Id es obligatorio")
    private Long especialidadId;
}
