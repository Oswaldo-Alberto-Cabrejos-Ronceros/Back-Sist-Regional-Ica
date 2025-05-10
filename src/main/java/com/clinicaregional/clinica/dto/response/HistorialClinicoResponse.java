package com.clinicaregional.clinica.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class HistorialClinicoResponse {
    private Long id;
    private Long pacienteId;
    private LocalDate fechaRegistro;
}
