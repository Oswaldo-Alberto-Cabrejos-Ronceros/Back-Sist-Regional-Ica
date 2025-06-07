package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.HistorialClinicoRequest;
import com.clinicaregional.clinica.dto.response.HistorialClinicoResponse;

public interface HistorialClinicoService {

    HistorialClinicoResponse crear(HistorialClinicoRequest historialClinicoRequest);
    HistorialClinicoResponse obtenerPorPacienteId(Long pacienteId);

}
