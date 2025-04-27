package com.clinicaregional.clinica.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "correo debe ser un email valido")
    private String correo;
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
