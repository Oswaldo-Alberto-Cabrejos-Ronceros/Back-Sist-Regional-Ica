package com.clinicaregional.clinica.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultadoResponse {
    private Long id;

    private String diagnostico;

    private String tratamiento;

    private String notasResultado;

    private Long citaId;

    private Long historialClinicoId;
}
