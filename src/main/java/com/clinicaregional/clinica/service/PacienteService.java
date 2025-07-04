package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.PacienteDTO;
import com.clinicaregional.clinica.dto.PacienteSimpleDTO;
import com.clinicaregional.clinica.dto.request.UpdatePacienteDTO;
import com.clinicaregional.clinica.dto.response.MyInfoPaciente;
import com.clinicaregional.clinica.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface PacienteService {
    List<PacienteDTO> listarPacientes();

    List<PacienteDTO> listarPacientesPorEstado();

    Optional<PacienteDTO> getPacientePorId(Long id);

    Optional<PacienteDTO> getPacientePorIdentificacion(String identificacion);

    MyInfoPaciente getMyInfoPaciente(Long pacienteId);

    PacienteDTO crearPaciente(PacienteDTO pacienteDTO);

    //Crear paciente sin usuario
    PacienteSimpleDTO crearPacienteSimple(PacienteSimpleDTO pacienteSimpleDTO);

    PacienteDTO actualizarPaciente(Long id, UpdatePacienteDTO updatepacienteDTO);

    void eliminarPaciente(Long id);

    PagedResponse<PacienteDTO> listarPacientesPaginado(Pageable pageable);

}
