package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
import java.util.List;
import java.util.Optional;

public interface CitaService {

    List<CitaResponse> listar();
    Optional<CitaResponse> obtenerPorId(Long id);
    CitaResponse guardar(CitaRequest citaRequest);
    CitaResponse actualizar(Long id, CitaRequest citaRequest);
    void eliminar(Long id);

}
