package com.clinicaregional.clinica.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CitaReprogramarRequest {
    @NotNull(message = "La fecha no puede estar en blanco")
    @FutureOrPresent(message = "La fecha no puede ser pasada")
    private LocalDateTime fechaHora;
}
