package com.clinicaregional.clinica.dto.response;

import java.time.LocalDateTime;

import com.clinicaregional.clinica.enums.TipoContrato;
import com.clinicaregional.clinica.enums.TipoMedico;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicoResponseDTO {
    
    private Long id;
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
    

}
