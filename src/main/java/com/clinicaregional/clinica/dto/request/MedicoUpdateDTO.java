package com.clinicaregional.clinica.dto.request;

import com.clinicaregional.clinica.enums.TipoContrato;
import com.clinicaregional.clinica.enums.TipoMedico;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicoUpdateDTO {

    private String nombres;
    private String apellidos;
    private String numeroColegiatura;
    private String numeroRNE;
    private Long tipoDocumentoId;
    private String numeroDocumento;
    private String telefono;
    private String direccion;
    private String descripcion;
    private String imagen;
    private TipoContrato tipoContrato;
    private TipoMedico tipoMedico;
    private String correo;

}
