package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.DisponibilidadRequest;
import com.clinicaregional.clinica.dto.response.DisponibilidadResponse;
import java.util.List;

public interface DisponibilidadService {

    DisponibilidadResponse registrar(DisponibilidadRequest request);

    DisponibilidadResponse obtenerPorId(Long id);

    List<DisponibilidadResponse> listar();

    DisponibilidadResponse actualizar(Long id, DisponibilidadRequest request);

    void eliminar(Long id);
}
