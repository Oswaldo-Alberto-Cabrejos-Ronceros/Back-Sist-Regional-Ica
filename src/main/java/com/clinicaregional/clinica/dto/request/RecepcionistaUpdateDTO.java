package com.clinicaregional.clinica.dto.request;

import com.clinicaregional.clinica.enums.TurnoTrabajo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecepcionistaUpdateDTO {
    private String nombres;
    private String apellidos;
    private String numeroDocumento;
    private Long tipoDocumentoId;
    private String telefono;
    private String direccion;
    private String imagenUrl;
    private TurnoTrabajo turnoTrabajo;
    private String correo;
}
