package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.request.HorarioBloqueRequest;
import com.clinicaregional.clinica.dto.response.HorarioBloqueResponse;
import com.clinicaregional.clinica.entity.Disponibilidad;
import com.clinicaregional.clinica.entity.HorarioBloque;
import org.springframework.stereotype.Component;

@Component
public class HorarioBloqueMapper {

    // Entity → Response
    public HorarioBloqueResponse mapToHorarioBloqueResponse(HorarioBloque bloque) {
        return new HorarioBloqueResponse(
                bloque.getId(),
                bloque.getFecha(),
                bloque.getHoraInicio(),
                bloque.getHoraFin(),
                bloque.getEstadoBloque().name(),
                bloque.getDisponibilidad().getId(),
                bloque.getCita() != null ? bloque.getCita().getCitaId() : null);
    }

    // Request → Entity
    public HorarioBloque mapToHorarioBloque(HorarioBloqueRequest request) {
        Disponibilidad disponibilidad = new Disponibilidad();
        disponibilidad.setId(request.getDisponibilidadId());

        return HorarioBloque.builder()
                .fecha(request.getFecha())
                .horaInicio(request.getHoraInicio())
                .horaFin(request.getHoraFin())
                .estadoBloque(request.getEstadoBloque())
                .disponibilidad(disponibilidad)
                .build();
    }
}
