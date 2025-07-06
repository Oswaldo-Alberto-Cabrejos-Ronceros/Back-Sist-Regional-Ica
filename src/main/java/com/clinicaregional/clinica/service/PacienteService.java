package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.PacienteConUserDTO;
import com.clinicaregional.clinica.dto.PacienteSinUserDTO;
import com.clinicaregional.clinica.dto.request.UpdatePacienteDTO;
import com.clinicaregional.clinica.dto.response.MyInfoPaciente;
import com.clinicaregional.clinica.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface PacienteService {
    List<PacienteConUserDTO> listarPacientes();

    List<PacienteConUserDTO> listarPacientesPorEstado();

    Optional<PacienteConUserDTO> getPacientePorId(Long id);

    Optional<PacienteConUserDTO> getPacientePorIdentificacion(String identificacion);

    MyInfoPaciente getMyInfoPaciente(Long pacienteId);

    PacienteConUserDTO crearPacientePorWeb(PacienteConUserDTO pacienteDTO);

    //Crear paciente sin usuario
    PacienteSinUserDTO crearPacienteSimple(PacienteSinUserDTO pacienteSimpleDTO);

    PacienteConUserDTO actualizarPaciente(Long id, UpdatePacienteDTO updatepacienteDTO);

    void eliminarPaciente(Long id);

    PagedResponse<PacienteConUserDTO> listarPacientesPaginado(Pageable pageable);

    Optional<PacienteConUserDTO> getPacientePorEmail(String email);

}
