package com.clinicaregional.clinica.dto.authentication;

import com.clinicaregional.clinica.dto.TipoDocumentoDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyPatientRequest {

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumentoDTO tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    private String documento;

}
