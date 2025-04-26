package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.EspecialidadRequest;
import com.clinicaregional.clinica.dto.EspecialidadResponse;

public interface EspecialidadService {
    
    EspecialidadResponse guardarEspecialidad(EspecialidadRequest especialidadRequest);

    EspecialidadResponse actualizarEspecialidad(Long id, EspecialidadRequest especialidadRequest);

    void eliminarEspecialidad(Long id);
}
