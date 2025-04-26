package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.PacienteDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface PacienteService {
    List<PacienteDTO> listarPacientes();

    Optional<PacienteDTO> getPacientePorId(Long id);

    Optional<PacienteDTO> getPacientePorIdentificacion(String identificacion);

    PacienteDTO crearPaciente(PacienteDTO pacienteDTO);

    PacienteDTO actualizarPaciente(Long id, PacienteDTO pacienteDTO);

    void eliminarPaciente(Long id);
}
