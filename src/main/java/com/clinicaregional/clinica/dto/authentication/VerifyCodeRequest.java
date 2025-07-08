package com.clinicaregional.clinica.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyCodeRequest {
    @NotBlank(message = "El código es obligatorio")
    private String code;
}
