package com.clinicaregional.clinica.dto;

import com.clinicaregional.clinica.enums.EstadoSeguro;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NotNull
@AllArgsConstructor
public class EstadoSeguroDTO {
    @NotNull(message = "Estado seguro es obligatorio")
    private EstadoSeguro estadoSeguro;
}
