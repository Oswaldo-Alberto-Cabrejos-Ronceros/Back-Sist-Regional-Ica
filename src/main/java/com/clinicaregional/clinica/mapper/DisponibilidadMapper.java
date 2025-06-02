package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.request.DisponibilidadRequest;
import com.clinicaregional.clinica.dto.response.DisponibilidadResponse;
import com.clinicaregional.clinica.entity.Disponibilidad;
import com.clinicaregional.clinica.entity.Medico;
import org.springframework.stereotype.Component;

@Component
public class DisponibilidadMapper {

    public Disponibilidad toEntity(DisponibilidadRequest request) {
        //generamos medico
        Medico medico = new Medico();
        medico.setId(request.getMedicoId());

        Disponibilidad disponibilidad = new Disponibilidad();
        disponibilidad.setDiaSemana(request.getDiaSemana());
        disponibilidad.setHoraInicio(request.getHoraInicio());
        disponibilidad.setHoraFin(request.getHoraFin());
        disponibilidad.setNotas(request.getNotas());
        disponibilidad.setMedico(medico);
        return disponibilidad;
    }

    public DisponibilidadResponse toResponse(Disponibilidad disponibilidad) {
        return new DisponibilidadResponse(
                disponibilidad.getId(),
                disponibilidad.getDiaSemana(),
                disponibilidad.getHoraInicio(),
                disponibilidad.getHoraFin(),
                disponibilidad.getNotas(),
                disponibilidad.getMedico().getId()
        );
    }

}
