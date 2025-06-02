package com.clinicaregional.clinica.dto.request;

import com.clinicaregional.clinica.enums.EstadoBloque;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class HorarioBloqueRequest {
    private String nombre;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EstadoBloque estadoBloque;
    private Long medicoId;
    private Long citaId;
    private Long disponibilidadId;
}

