package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.EspecialidadRequest;
import com.clinicaregional.clinica.dto.response.EspecialidadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface EspecialidadService {

    List<EspecialidadResponse> listarEspecialidades();

    Optional<EspecialidadResponse> getEspecialidadById(Long id);

    EspecialidadResponse guardarEspecialidad(EspecialidadRequest especialidadRequest, MultipartFile imagen);

    EspecialidadResponse actualizarEspecialidad(Long id, EspecialidadRequest especialidadRequest, MultipartFile imagen);

    void eliminarEspecialidad(Long id);
}
