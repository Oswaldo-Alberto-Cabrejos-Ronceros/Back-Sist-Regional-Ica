package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.PacienteAlergiaDTO;

import java.util.List;
import java.util.Optional;

public interface PacienteAlergiaService {
    List<PacienteAlergiaDTO> listarPacienteAlergias();

    List<PacienteAlergiaDTO> listarPacienteAlergiasPorPaciente(Long id);

    Optional<PacienteAlergiaDTO> getPacienteAlergiaById(Long id);

    PacienteAlergiaDTO createPacienteAlergia(PacienteAlergiaDTO pacienteAlergiaDTO);

    PacienteAlergiaDTO updatePacienteAlergia(Long id, PacienteAlergiaDTO pacienteAlergiaDTO);

    void deletePacienteAlergia(Long id);
}
