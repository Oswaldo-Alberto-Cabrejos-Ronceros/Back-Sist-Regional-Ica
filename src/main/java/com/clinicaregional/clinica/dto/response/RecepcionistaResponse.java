package com.clinicaregional.clinica.dto.response;

import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class RecepcionistaResponse {
    private Long id;
    private String nombres;
    private String apellidos;
    private String numeroDocumento;
    private TipoDocumento tipoDocumento;
    private String telefono;
    private String direccion;
    private String turnoTrabajo;
    private LocalDate fechaContratacion;
    private Usuario usuario;
}
