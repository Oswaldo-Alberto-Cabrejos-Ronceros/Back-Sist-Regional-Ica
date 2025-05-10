package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.HistorialClinicoRequest;
import com.clinicaregional.clinica.dto.response.HistorialClinicoResponse;

import java.util.Optional;

public interface HistorialClinicoService {
    Optional<HistorialClinicoResponse> getHistorialClinicoByPaciente(Long pacienteId);
    HistorialClinicoResponse createHistorislClinico(HistorialClinicoRequest historialClinicoRequest);

}
