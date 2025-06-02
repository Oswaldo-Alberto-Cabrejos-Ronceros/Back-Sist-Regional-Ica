package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.request.HorarioBloqueRequest;
import com.clinicaregional.clinica.dto.response.HorarioBloqueResponse;
import com.clinicaregional.clinica.entity.*;
import org.springframework.stereotype.Component;

@Component
public class HorarioBloqueMapper {

    public HorarioBloque toEntity(HorarioBloqueRequest request, Medico medico, Cita cita, Disponibilidad disponibilidad) {
        HorarioBloque bloque = new HorarioBloque();
        bloque.setNombre(request.getNombre());
        bloque.setFecha(request.getFecha());
        bloque.setHoraInicio(request.getHoraInicio());
        bloque.setHoraFin(request.getHoraFin());
        bloque.setEstadoBloque(request.getEstadoBloque());
        bloque.setMedico(medico);
        bloque.setCita(cita);
        bloque.setDisponibilidad(disponibilidad);
        return bloque;
    }

    public HorarioBloqueResponse toResponse(HorarioBloque entity) {
        String nombreMedico = entity.getMedico().getNombres() + " " + entity.getMedico().getApellidos();

        return new HorarioBloqueResponse(
                entity.getId(),
                entity.getNombre(),
                entity.getFecha(),
                entity.getHoraInicio(),
                entity.getHoraFin(),
                entity.getEstadoBloque().name(),
                nombreMedico,
                entity.getCita() != null ? entity.getCita().getId() : null,
                entity.getDisponibilidad().getId()
        );
    }
}
