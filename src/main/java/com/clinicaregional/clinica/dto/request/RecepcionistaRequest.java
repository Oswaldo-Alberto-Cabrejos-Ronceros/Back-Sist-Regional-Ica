package com.clinicaregional.clinica.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RecepcionistaRequest {
    private String nombres;
    private String apellidos;
    private String numeroDocumento;
    private Long tipoDocumentoId;
    private String telefono;
    private String direccion;
    private String turnoTrabajo;
    private LocalDate fechaContratacion;
    private Long usuarioId;
}
