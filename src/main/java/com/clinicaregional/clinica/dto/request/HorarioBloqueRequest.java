package com.clinicaregional.clinica.dto.request;

import com.clinicaregional.clinica.enums.EstadoBloque;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioBloqueRequest {

    @NotNull(message = "Debe ingresar la fecha del bloque")
    private LocalDate fecha;

    @NotNull(message = "Debe ingresar la hora de inicio")
    private LocalTime horaInicio;

    @NotNull(message = "Debe ingresar la hora de fin")
    private LocalTime horaFin;

    @NotNull(message = "Debe especificar el estado del bloque")
    private EstadoBloque estadoBloque;

    @NotNull(message = "Debe indicar la disponibilidad asociada")
    private Long disponibilidadId;
}
