package com.clinicaregional.clinica.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicoEspecialidadResponse {

    private Long medicoId;
    private String nombreMedico;
    private String numeroColegiatura; //nuevo
    private String numeroRNE; //nuevo
    private Long especialidadId;
    private String nombreEspecialidad;
    private LocalDate desdeFecha;

}
