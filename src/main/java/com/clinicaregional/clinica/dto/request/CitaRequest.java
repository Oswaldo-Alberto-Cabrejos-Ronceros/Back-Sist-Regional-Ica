package com.clinicaregional.clinica.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

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
    private LocalDate fecha;

    @NotNull(message = "La hora no puede estar en blanco")
    private LocalTime hora;

    @Size(max = 255, message = "Las notas no deben superar los 255 caracteres")
    private String notas;

    @Size(max = 1000, message = "Los antecedentes no deben superar los 1000 caracteres")
    private String antecedentes;

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El ID del médico es obligatorio")
    private Long medicoId;

    @NotNull(message = "El ID del servicio es obligatorio")
    private Long servicioId;

    private Long seguroId;     // Opcional
    private Long coberturaId;  // Opcional
}
