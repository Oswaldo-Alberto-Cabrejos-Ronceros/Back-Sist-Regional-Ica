package com.clinicaregional.clinica.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpinionResponse {

    private Long id;
    private Long pacienteId;
    private Long medicoId;
    //private Long citaId;
    private String comentario;
    private Integer calificacion;
    private String fecha;
    private Boolean visible;

}
