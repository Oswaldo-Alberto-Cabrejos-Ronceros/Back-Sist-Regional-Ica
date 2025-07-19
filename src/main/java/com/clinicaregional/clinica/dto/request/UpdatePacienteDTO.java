package com.clinicaregional.clinica.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdatePacienteDTO {

    private String telefono;

    private String direccion;

    private String imagenUrl;

    private String antecedentes;

    private String contactoDeEmergenciaNombre;

    private String contactoDeEmergenciaTelefono;

}