package com.clinicaregional.clinica.dto;

import java.time.LocalDateTime;

import com.clinicaregional.clinica.entity.TipoContrato;
import com.clinicaregional.clinica.entity.TipoMedico;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicoRequestDTO {

    @NotBlank(message = "Nombres es obligatorio")
    @Size(min = 2, max = 48, message = "Nombres debe tener entre 2 y 48 caracteres")
    private String nombres;

    @NotBlank(message = "Apellidos es obligatorio")
    @Size(min = 2, max = 64, message = "Apellidos debe tener entre 2 y 64 caracteres")
    private String apellidos;

    @Null
    @Pattern(regexp = "\\d{11}", message = "El numero de colegiatura solo debe contener numeros y 11 digitos")
    private String numeroColegiatura;

    @Null
    @Pattern(regexp = "\\d{9}", message = "El numero RNE solo debe contener numeros y 9 digitos")
    private String numeroRNE;

    @NotBlank(message = "El telefono es obligatorio")
    @Pattern(regexp = "\\d{9}", message = "El telefono solo debe contener numeros y 9 digitos")
    private String telefono;

    @NotBlank(message = "La direccion es obligatoria")
    @Size(max = 156, message = "La direccion debe ser menor a 156 caracteres")
    private String direccion;

    @NotBlank(message = "Descripcion es obligatioria")
    @Size(max = 255, message = "La descripcion debe ser menor a 255")
    private String descripcion;

    private String imagen; //despues poner imagen predeterminada

    @NotNull(message = "Fecha de contratacion es obligati")
    @PastOrPresent(message = "La fecha de contratación debe ser antes o hoy")
    private LocalDateTime fechaContratacion;

    @NotNull(message = "El tipo de contrato es obligatorio")
    private TipoContrato tipoContrato;

    @NotNull(message = "El tipo de medico es obligatorio")
    private TipoMedico tipoMedico;

    @Null
    private Long usuarioId;

}