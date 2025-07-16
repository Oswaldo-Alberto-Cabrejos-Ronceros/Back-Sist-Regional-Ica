package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.RecepcionistaRequest;
import com.clinicaregional.clinica.dto.response.MyInfoRecepcionista;
import com.clinicaregional.clinica.dto.response.RecepcionistaResponse;

import java.util.List;
import java.util.Optional;

public interface RecepcionistaService {
    List<RecepcionistaResponse> listar();

    Optional<RecepcionistaResponse> obtenerPorId(Long id);

    MyInfoRecepcionista obtenerMyInfoRecepcionista(Long id);

    RecepcionistaResponse guardar(RecepcionistaRequest recepcionistaRequest);

    RecepcionistaResponse actualizar(Long id, RecepcionistaRequest recepcionistaRequest);

    void eliminar(Long id);
}