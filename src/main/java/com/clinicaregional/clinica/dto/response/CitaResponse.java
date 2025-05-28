package com.clinicaregional.clinica.dto.response;

import java.sql.Time;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CitaResponse {

    private Long id;
    private Date fecha;
    private Time hora;
    private boolean estadoCita;
    private String notas;
    
}
