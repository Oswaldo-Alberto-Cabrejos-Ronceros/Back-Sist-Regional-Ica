package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;

import java.util.List;

public interface CitaService {

    CitaResponse registrar(CitaRequest request);

    CitaResponse obtenerPorId(Long id);

    List<CitaResponse> listarTodas();

    CitaResponse actualizar(Long id, CitaRequest request);

    void eliminar(Long id);
}
