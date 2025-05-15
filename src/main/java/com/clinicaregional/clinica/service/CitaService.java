package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.CitaReprogramarRequest;
import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
import com.clinicaregional.clinica.entity.Servicio;

import java.util.List;
import java.util.Optional;

public interface CitaService {

    List<CitaResponse> listar();

    Optional<CitaResponse> obtenerPorId(Long id);

    CitaResponse guardar(CitaRequest citaRequest);

    CitaResponse actualizar(Long id, CitaRequest citaRequest);

    void cancelar(Long id);

    CitaResponse reprogramar(Long id, CitaReprogramarRequest citaReprogramarRequest);

    void marcarAtentida (Long id);

    void marcoNoAsistio(Long id);

    List<Servicio> listarServiciosActivos();

    Servicio obtenerServicioPorId(Long id);

}
