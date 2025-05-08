package com.clinicaregional.clinica.dto.response;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import com.clinicaregional.clinica.enums.EstadoCita;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CitaResponse {

    private Long id;
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
