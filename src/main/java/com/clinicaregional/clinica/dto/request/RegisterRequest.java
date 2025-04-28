package com.clinicaregional.clinica.dto.request;

import com.clinicaregional.clinica.dto.PacienteDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotNull(message = "Usuario es obligatorio")
    private UsuarioRequestDTO usuario;

    @NotNull(message = "Paciente es obligatorio")
    private PacienteDTO paciente;
}
