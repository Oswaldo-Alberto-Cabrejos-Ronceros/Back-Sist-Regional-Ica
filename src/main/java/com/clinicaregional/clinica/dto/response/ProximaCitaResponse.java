package com.clinicaregional.clinica.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

import com.clinicaregional.clinica.enums.EstadoCita;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProximaCitaResponse {

    private Long citaId;
    private LocalDate fecha;
    private LocalTime hora;
    private String medicoNombreCompleto;
    private String servicioNombre;
    private EstadoCita estadoCita;

}
