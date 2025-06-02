package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.request.HorarioBloqueRequest;
import com.clinicaregional.clinica.dto.response.HorarioBloqueResponse;
import com.clinicaregional.clinica.entity.*;
import org.springframework.stereotype.Component;

@Component
public class HorarioBloqueMapper {

    public HorarioBloque toEntity(HorarioBloqueRequest request) {
        HorarioBloque bloque = new HorarioBloque();
        //medico
        Medico medico = new Medico();
        //cita
        Cita cita = new Cita();
        cita.setId(request.getCitaId());
        //disponibilidad
        Disponibilidad disponibilidad = new Disponibilidad();
        disponibilidad.setId(request.getDisponibilidadId());
        medico.setId(request.getMedicoId());
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

        return new HorarioBloqueResponse(
                entity.getId(),
                entity.getNombre(),
                entity.getFecha(),
                entity.getHoraInicio(),
                entity.getHoraFin(),
                entity.getEstadoBloque().name(),
                entity.getMedico().getId(),
                entity.getCita() != null ? entity.getCita().getId() : null,
                entity.getDisponibilidad().getId()
        );
    }
}
