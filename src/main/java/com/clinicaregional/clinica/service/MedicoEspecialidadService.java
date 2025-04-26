package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.MedicoEspecialidadRequest;
import com.clinicaregional.clinica.dto.MedicoEspecialidadResponse;
import java.util.List;

public interface MedicoEspecialidadService {

    MedicoEspecialidadResponse registrarRelacionME(MedicoEspecialidadRequest request);

    MedicoEspecialidadResponse actualizarRelacionME(Long medicoId, Long especialidadId, MedicoEspecialidadRequest request);

    void eliminarRelacionME(Long medicoId, Long especialidadId);

    List<MedicoEspecialidadResponse> obtenerEspecialidadDelMedico(Long medicoId);

    List<MedicoEspecialidadResponse> obtenerMedicosPorEspecialidad(Long especialidadId);
}