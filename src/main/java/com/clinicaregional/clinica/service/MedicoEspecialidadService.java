package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.MedicoEspecialidadRequest;
import com.clinicaregional.clinica.dto.response.MedicoEspecialidadResponse;
import java.util.List;

public interface MedicoEspecialidadService {

    List<MedicoEspecialidadResponse> obtenerTodasRelacionesME();

    MedicoEspecialidadResponse registrarRelacionME(MedicoEspecialidadRequest request);

    MedicoEspecialidadResponse actualizarRelacionME(Long medicoId, Long especialidadId, MedicoEspecialidadRequest request);

    void eliminarRelacionME(Long medicoId, Long especialidadId);

    List<MedicoEspecialidadResponse> obtenerEspecialidadDelMedico(Long medicoId);

    List<MedicoEspecialidadResponse> obtenerMedicosPorEspecialidad(Long especialidadId);
}