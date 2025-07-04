package com.clinicaregional.clinica.dto;

import com.clinicaregional.clinica.enums.Sexo;
import com.clinicaregional.clinica.enums.ModalidadDeAtencion;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PacienteSinUserDTO {

    private Long id;

    @NotBlank(message = "Nombres es obligatorio")
    @Size(min = 2, max = 48, message = "Nombres debe tener entre 2 y 48 caracteres")
    private String nombres;

    @NotBlank(message = "Apellidos es obligatorio")
    @Size(min = 2, max = 64, message = "Apellidos debe tener entre 2 y 64 caracteres")
    private String apellidos;

    @NotNull
    @PastOrPresent(message = "La fecha de nacimiento debe ser antes o hoy")
    private LocalDate fechaNacimiento;

    @NotNull
    private Sexo sexo;

    @NotNull
    private TipoDocumentoDTO tipoDocumento;

    @NotBlank(message = "El número de identificacion es obligatoria")
    private String numeroIdentificacion;

    @NotNull(message = "La nacionalidad es obligatoria")
    @Size(max = 32, message = "La nacionalidad debe tener menos de 32 palabras")
    private String nacionalidad ;

    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

    @NotBlank(message = "La direccion es obligatoria")
    private String direccion;

    @NotBlank(message = "El Email es obligatorio")
    private String email;

    @NotNull
    private ModalidadDeAtencion modalidadDeAtencion;

    @Nullable
    private SeguroDTO seguro;

    @Nullable(message = "El número de poliza es obligatorio")
    private String numeroDePoliza;

    private String contactoDeEmergenciaNombre;

    private String contactoDeEmergenciaTelefono;

}