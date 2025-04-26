package com.clinicaregional.clinica.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RecepcionistaRequest {
    @NotBlank(message = "Nombres es obligatorio")
    @Size(min = 2, max = 48, message = "Nombres debe tener entre 2 y 48 caracteres")
    private String nombres;

    @NotBlank(message = "Apellidos es obligatorio")
    @Size(min = 2, max = 64, message = "Apellidos debe tener entre 2 y 64 caracteres")
    private String apellidos;

    @NotBlank(message = "El numero de documento es obligatorio")
    @Pattern(regexp = "\\d+", message = "El numero de documento debe contener numeros")
    private String numeroDocumento;

    @NotNull
    private Long tipoDocumentoId;

    @NotBlank(message = "El telefono es obligatorio")
    @Pattern(regexp = "\\d{9}", message = "El telefono solo debe contener numeros y 9 digitos")
    private String telefono;

    @NotBlank(message = "La direccion es obligatoria")
    @Size(max = 156, message = "La direccion debe ser menor a 156 caracteres")
    private String direccion;


    private String turnoTrabajo;

    @NotNull(message = "Fecha de contratacion es obligatorio")
    @PastOrPresent(message = "La fecha de contratación debe ser antes o hoy")
    private LocalDate fechaContratacion;

    @Null
    private Long usuarioId;
}
