package com.clinicaregional.clinica.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyInfoMedico {
    private String nombres;
    private String apellidos;
    private String email;
    private String numeroColegiatura;
    private String numeroRNE;
    private String tipoDocumento;
    private String numeroDocumento;
    private String telefono;
    private String direccion;
    private String imagenUrl;
    private LocalDate fechaContratacion;
}
