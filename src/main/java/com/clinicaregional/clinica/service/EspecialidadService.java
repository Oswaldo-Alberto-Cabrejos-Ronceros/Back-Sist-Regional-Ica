package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.EspecialidadRequest;
import com.clinicaregional.clinica.dto.response.EspecialidadResponse;

public interface EspecialidadService {
    
    EspecialidadResponse guardarEspecialidad(EspecialidadRequest especialidadRequest);

    EspecialidadResponse actualizarEspecialidad(Long id, EspecialidadRequest especialidadRequest);

    void eliminarEspecialidad(Long id);
}
