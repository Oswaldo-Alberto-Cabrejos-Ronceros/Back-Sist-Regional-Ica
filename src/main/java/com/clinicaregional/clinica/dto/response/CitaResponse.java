package com.clinicaregional.clinica.dto.response;

import com.clinicaregional.clinica.enums.EstadoCita;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CitaResponse {

    private Long citaId;
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoCita estadoCita;
    private String notas;
    private String antecedentes;

    private Long pacienteId;
    private Long medicoId;
    private Long servicioId;
    private Long seguroId;
    private Long coberturaId;
}
