package com.clinicaregional.clinica.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HistorialClinicoRequest {

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "La fecha de registro es obligatoria")
    private LocalDate fechaRegistro;
}
