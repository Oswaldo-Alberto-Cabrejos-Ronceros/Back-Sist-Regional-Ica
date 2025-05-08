package com.clinicaregional.clinica.dto.response;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import com.clinicaregional.clinica.enums.EstadoCita;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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
