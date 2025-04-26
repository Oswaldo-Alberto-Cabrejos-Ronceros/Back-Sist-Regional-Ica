package com.clinicaregional.clinica.dto;

import com.clinicaregional.clinica.enums.TipoAlergia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlergiaDTO {
    private Long id;

    @NotBlank(message = "Nombres es obligatorio")
    @Size(max = 48, message = "Nombres debe tener menos de 48 caracteres")
    private String nombre;

    @NotNull
    private TipoAlergia tipoAlergia;
}
