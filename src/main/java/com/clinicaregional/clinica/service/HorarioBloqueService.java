package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.HorarioBloqueRequest;
import com.clinicaregional.clinica.dto.response.HorarioBloqueResponse;
import com.clinicaregional.clinica.entity.HorarioBloque;
import com.clinicaregional.clinica.enums.EstadoBloque;

import java.util.List;

public interface HorarioBloqueService {
    List<HorarioBloqueResponse> obtenerHorariosBloques();
    HorarioBloqueResponse crearHorarioBloque(HorarioBloqueRequest horarioBloqueRequest);
    HorarioBloqueResponse obtenerHorarioBloquePorId(Long id);
    boolean estaDiponibleHorarioBloque(Long id);
    HorarioBloqueResponse actualizarEstadoHorarioBloque(Long id, EstadoBloque estado);
    void liberarHorarioBloque(Long id);
}
