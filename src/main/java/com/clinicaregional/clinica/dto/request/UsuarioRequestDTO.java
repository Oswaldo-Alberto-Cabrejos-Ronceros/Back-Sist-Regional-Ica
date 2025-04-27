package com.clinicaregional.clinica.dto.request;

import com.clinicaregional.clinica.dto.RolDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsuarioRequestDTO {
    @NotBlank(message = "Nombre es obligatorio")
    @Email(message = "correo debe ser un email valido")
    private String correo;
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 32, message = "La contraseña debe tener entre 6 y 32 caracteres")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).*$", message = "La contraseña debe tener por lo menos una letra mayuscula y un número")
    private String password;
    private boolean estado;
    private RolDTO rol;

}
