package com.clinicaregional.clinica.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistorialClinicoRequest {
    @NotNull(message = "Paciente es obligatorio")
    private Long pacienteId;
}
