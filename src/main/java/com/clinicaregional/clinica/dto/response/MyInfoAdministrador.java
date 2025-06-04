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
public class MyInfoAdministrador {
    private String nombres;
    private String apellidos;
    private String email;
    private String tipoDocumento;
    private String numeroDocumento;
    private String telefono;
    private String direccion;
    private LocalDate fechaContratacion;
}
