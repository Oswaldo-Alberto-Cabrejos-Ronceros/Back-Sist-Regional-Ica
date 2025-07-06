package com.clinicaregional.clinica.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompletePatientRegistrationRequest {
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 32, message = "La contraseña debe tener entre 6 y 32 caracteres")
    private String password;

    @NotBlank
    @Email
    private String email;
}
