package com.clinicaregional.clinica.dto.request;

import java.time.LocalTime;

import com.clinicaregional.clinica.enums.DiaSemana;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @NotNull(message = "Debe ingresar la duración de los bloques en minutos (30, 45 o 60)")
    @Min(value = 30, message = "Duración mínima permitida es 30 minutos")
    @Max(value = 60, message = "Duración máxima permitida es 60 minutos")
    private Integer duracionMinutos;

    @AssertTrue(message = "La hora de inicio debe ser anterior a la hora de fin")
    public boolean isHoraInicioAntesQueFin() {
        return horaInicio != null && horaFin != null && horaInicio.isBefore(horaFin);
    }

}
