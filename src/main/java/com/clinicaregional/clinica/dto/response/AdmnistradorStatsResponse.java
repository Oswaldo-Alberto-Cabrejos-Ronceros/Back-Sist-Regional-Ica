package com.clinicaregional.clinica.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdmnistradorStatsResponse {
private Long totalPaciente;
private Long citasHoy;
private Double ingresosMes;
}
