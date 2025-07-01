package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.HorarioBloqueRequest;
import com.clinicaregional.clinica.dto.response.HorarioBloqueResponse;

import java.time.LocalDate;
import java.util.List;

public interface HorarioBloqueService {

    HorarioBloqueResponse obtenerPorId(Long id);

    List<HorarioBloqueResponse> listarPorDisponibilidad(Long disponibilidadId);

    List<HorarioBloqueResponse> listarPorMedico(Long medicoId);

    List<HorarioBloqueResponse> listarPorFecha(LocalDate fecha);

    List<HorarioBloqueResponse> listarPorEspecialidad(Long especialidadId);

    HorarioBloqueResponse actualizarEstado(Long id, String nuevoEstado);

    boolean estaDisponible(Long id);

    void liberar(Long id); // útil si una cita se cancela y se quiere marcar como DISPONIBLE

    HorarioBloqueResponse registrar(HorarioBloqueRequest request);

}
