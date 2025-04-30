package com.clinicaregional.clinica.dto.response;

import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.enums.TurnoTrabajo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecepcionistaResponse {
    private Long id;
    private String nombres;
    private String apellidos;
    private String numeroDocumento;
    private Long tipoDocumentoId;
    private String telefono;
    private String direccion;
    private TurnoTrabajo turnoTrabajo;
    private LocalDate fechaContratacion;
    private Long usuarioId;

}
