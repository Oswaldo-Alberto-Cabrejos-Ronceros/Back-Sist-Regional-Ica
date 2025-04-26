package com.clinicaregional.clinica.dto;

import java.time.LocalDateTime;

import com.clinicaregional.clinica.entity.TipoContrato;
import com.clinicaregional.clinica.entity.TipoMedico;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicoRequestDTO {
    
    private String nombres;
    private String apellidos;
    private String numeroColegiatura;
    private String numeroRNE;
    private String telefono;
    private String direccion;
    private String descripcion;
    private String imagen;
    private LocalDateTime fechaContratacion;
    private TipoContrato tipoContrato;
    private TipoMedico tipoMedico;
    private Long usuarioId;

}
