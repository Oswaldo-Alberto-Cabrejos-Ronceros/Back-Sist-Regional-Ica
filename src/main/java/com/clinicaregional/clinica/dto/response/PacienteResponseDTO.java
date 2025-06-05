package com.clinicaregional.clinica.dto.response;

import com.clinicaregional.clinica.enums.Sexo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PacienteResponseDTO {
    private String nombres;
    private String apellidos;
    private String numeroIdentificacion;
    private String telefono;
    private Sexo sexo;
    private int edad; // calculada
    private String antecedentes;
}
