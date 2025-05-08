package com.clinicaregional.clinica.dto.request;

import com.clinicaregional.clinica.enums.EstadoCita;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
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
    @FutureOrPresent(message = "La hora no puede ser pasada")
    private LocalTime hora;
    private EstadoCita estadoCita;
    private String notas;
    private String antecedentes;
    @NotNull(message = "El paciente es obligatorio")
    private Long pacienteId;
    @NotNull(message = "El medico es obligatorio")
    private Long medicoId;
    @NotNull(message = "El service es obligatorio")
    private Long servicioId;
    private Long seguroId;
    private Long coberturaId;
}
