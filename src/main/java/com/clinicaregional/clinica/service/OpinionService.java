package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.OpinionRequest;
import com.clinicaregional.clinica.dto.response.OpinionResponse;
import java.util.List;
import java.util.Optional;

public interface OpinionService {

    List<OpinionResponse> listar();
    Optional<OpinionResponse> obtenerPorId(Long id);
    OpinionResponse guardar(OpinionRequest opinionRequest);
    void eliminar(Long id);

}
