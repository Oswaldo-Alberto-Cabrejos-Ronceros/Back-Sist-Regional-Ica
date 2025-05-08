package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
import com.clinicaregional.clinica.entity.Cita;
import org.springframework.stereotype.Component;

@Component
public class CitaMapper {

    //de requesta a Entidad
    public Cita toEntity(CitaRequest citaRequest) {
        return Cita.builder().fecha(citaRequest.getFecha())
                .hora(citaRequest.getHora())
                .estadoCita(citaRequest.getEstadoCita())
                .notas(citaRequest.getNotas())
                .antecedentes(citaRequest.getAntecedentes()).build();
    }

    //de entidad a Response
    public CitaResponse toResponse(Cita cita) {
        return new CitaResponse(cita.getId(),cita.getFecha(),cita.getHora(),
                cita.getEstadoCita(),cita.getNotas(),cita.getAntecedentes());
    }
}
