package com.clinicaregional.clinica.dto.response;

import com.clinicaregional.clinica.enums.Sexo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyInfoPaciente {
    private String names;
    private String apellidos;
    private String numeroIdentificacion;
    private String email;
    private LocalDate fechaNacimiento;
    private Sexo sexo;
    private String nacionalidad;
    private String direccion;
    private String imagenUrl;
}
