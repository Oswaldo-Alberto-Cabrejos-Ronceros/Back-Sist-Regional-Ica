package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.response.HorarioBloqueResponse;
import com.clinicaregional.clinica.entity.HorarioBloque;
import com.clinicaregional.clinica.enums.EstadoBloque;

import java.util.List;
import java.util.Optional;

public interface HorarioBloqueService {
    List<HorarioBloqueResponse> obtenerHorariosBloques();
    HorarioBloqueResponse crearHorarioBloque(HorarioBloque horarioBloque);
    Optional<HorarioBloqueResponse> obtenerHorarioBloquePorId(Long id);
    boolean estaDiponibleHorarioBloque(Long id);
    HorarioBloqueResponse actualizarEstadoHorarioBloque(Long id, EstadoBloque estado);
    void liberarHorarioBloque(Long id);
}
