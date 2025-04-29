package com.clinicaregional.clinica.dto.request;

import com.clinicaregional.clinica.dto.AdministradorDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterAdministradorRequest {

    @NotNull(message = "Usuario es obligatorio")
    private UsuarioRequestDTO usuario;

    @NotNull(message = "Administrador es obligatorio")
    private AdministradorDTO administrador;
}
