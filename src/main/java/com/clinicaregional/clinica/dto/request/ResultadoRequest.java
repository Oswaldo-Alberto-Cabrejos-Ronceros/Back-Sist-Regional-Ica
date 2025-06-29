package com.clinicaregional.clinica.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoRequest {

    private String diagnostico;

    private String tratamiento;

    private String notasResultado;

    private Long citaId;

    private Long historialClinicoId;
}
