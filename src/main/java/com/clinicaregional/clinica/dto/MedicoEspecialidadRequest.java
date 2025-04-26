package com.clinicaregional.clinica.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicoEspecialidadRequest {

    private Long medicoId;
    private Long especialidadId;
    private LocalDate desdeFecha;

}
