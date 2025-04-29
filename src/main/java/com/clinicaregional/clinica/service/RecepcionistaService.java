package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.RecepcionistaRequest;
import com.clinicaregional.clinica.dto.response.RecepcionistaResponse;
import com.clinicaregional.clinica.entity.Recepcionista;
import java.util.List;
import java.util.Optional;

public interface RecepcionistaService {
    List<RecepcionistaResponse> listar();
    Optional<RecepcionistaResponse> obtenerPorId(Long id);
    RecepcionistaResponse guardar(RecepcionistaRequest recepcionistaRequest);
    void eliminar(Long id);
}
