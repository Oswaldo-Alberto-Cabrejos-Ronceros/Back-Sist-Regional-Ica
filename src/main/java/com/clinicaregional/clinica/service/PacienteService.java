package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.PacienteDTO;
import com.clinicaregional.clinica.dto.response.MyInfoPaciente;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface PacienteService {
    List<PacienteDTO> listarPacientes();

    Optional<PacienteDTO> getPacientePorId(Long id);

    Optional<PacienteDTO> getPacientePorIdentificacion(String identificacion);

    MyInfoPaciente getMyInfoPaciente(Long pacienteId);

    PacienteDTO crearPaciente(PacienteDTO pacienteDTO);

    PacienteDTO actualizarPaciente(Long id, PacienteDTO pacienteDTO);

    void eliminarPaciente(Long id);
}
