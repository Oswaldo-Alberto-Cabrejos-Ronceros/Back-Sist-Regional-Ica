package com.clinicaregional.clinica.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicoEspecialidadRequest {

    @NotNull(message = "Medico id es obligatorio")
    private Long medicoId;

    @NotNull(message = "Especilidad id es obligatorio")
    private Long especialidadId;

    @NotNull(message = "DesdeFecha es obligatorio")
    private LocalDate desdeFecha;

}