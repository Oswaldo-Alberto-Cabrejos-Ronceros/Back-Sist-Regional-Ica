package com.clinicaregional.clinica.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServicioSeguroDTO {

    private Long id;

    @NotNull( message = "ServicioId es obligatorio")
    private Long servicioId;

    @NotNull(message = "SeguroId es obligatorio")
    private Long seguroId;

    @NotNull(message = "CoberturaId es obligatorio")
    private Long coberturaId;
}
