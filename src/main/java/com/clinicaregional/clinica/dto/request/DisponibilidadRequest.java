package com.clinicaregional.clinica.dto.request;

import java.time.LocalTime;

import com.clinicaregional.clinica.enums.DiaSemana;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DisponibilidadRequest {

    @NotNull(message = "Día de la semana es obligatorio")
    private DiaSemana diaSemana;
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;
    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;
    private String notas;
    @NotNull(message = "El id del medico es obligatorio")
    private Long medicoId;

}
