package com.clinicaregional.clinica.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.sql.Time;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CitaRequest {

    @NotNull(message = "La fecha no puede estar en blanco")
    @FutureOrPresent(message = "La fecha no puede ser pasada")
    private Date fecha;
    @NotNull(message = "La hora no puede estar en blanco")
    @FutureOrPresent(message = "La hora no puede ser pasada")
    private Time hora;
    private boolean estadoCita;
    private String notas;
    private String antecedentes;

}
