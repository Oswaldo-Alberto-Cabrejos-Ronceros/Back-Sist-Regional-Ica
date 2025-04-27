package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.EspecialidadRequest;
import com.clinicaregional.clinica.dto.response.EspecialidadResponse;

import java.util.List;
import java.util.Optional;

public interface EspecialidadService {

    List<EspecialidadResponse> listarEspecialidades();

    Optional<EspecialidadResponse> getEspecialidadById(Long id);

    EspecialidadResponse guardarEspecialidad(EspecialidadRequest especialidadRequest);

    EspecialidadResponse actualizarEspecialidad(Long id, EspecialidadRequest especialidadRequest);

    void eliminarEspecialidad(Long id);
}
