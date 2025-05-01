package com.clinicaregional.clinica.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeguroCoberturaDTO {
    private Long id;
    @NotNull(message = "El id del seguro es obligatorio")
    private Long seguroId;
    @NotNull(message = "El ide la cobertura es obligatorio")
    private Long coberturaId;
}
