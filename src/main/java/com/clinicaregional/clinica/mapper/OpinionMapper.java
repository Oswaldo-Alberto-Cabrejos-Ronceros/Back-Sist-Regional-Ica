package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.request.OpinionRequest;
import com.clinicaregional.clinica.dto.response.OpinionResponse;
import com.clinicaregional.clinica.entity.Opinion;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.entity.Medico;
//import com.clinicaregional.clinica.entity.Cita;
import org.springframework.stereotype.Component;

@Component
public class OpinionMapper {

    public Opinion toEntity(OpinionRequest dto, Paciente paciente, Medico medico /*, Cita cita */) {
        return Opinion.builder()
                .comentario(dto.getComentario())
                .calificacion(dto.getCalificacion())
                .paciente(paciente)
                .medico(medico)
                //.cita(cita) // Descomentar cuando Cita esté lista
                .visible(true)
                .build();
    }

    public OpinionResponse toResponse(Opinion opinion) {
        return OpinionResponse.builder()
                .id(opinion.getId())
                .comentario(opinion.getComentario())
                .calificacion(opinion.getCalificacion())
                .pacienteId(opinion.getPaciente().getId())
                .medicoId(opinion.getMedico().getId())
                .visible(opinion.getVisible())
                // .citaId(opinion.getCita().getId()) // Descomentar cuando Cita esté lista
                .build();
    }
}
