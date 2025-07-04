package com.clinicaregional.clinica.dto.response;

import java.time.LocalTime;

import com.clinicaregional.clinica.enums.DiaSemana;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DisponibilidadResponse {
    private Long id;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String notas;
    private Long medicoId;
    private String ubicacion;

}