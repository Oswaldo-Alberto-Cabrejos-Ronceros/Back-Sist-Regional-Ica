package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
import com.clinicaregional.clinica.entity.Cita;
import com.clinicaregional.clinica.enums.EstadoCita;
import org.springframework.stereotype.Component;

@Component
public class CitaMapper {

    public Cita toEntity(CitaRequest request) {
        Cita cita = new Cita();
        cita.setFecha(request.getFecha());
        cita.setHora(request.getHora());
        cita.setNotas(request.getNotas());
        cita.setAntecedentes(request.getAntecedentes());
        cita.setEstadoCita(EstadoCita.PENDIENTE);
        return cita;
    }

    public CitaResponse toResponse(Cita cita) {
        return new CitaResponse(
                cita.getCitaId(),
                cita.getFecha(),
                cita.getHora(),
                cita.getEstadoCita(),
                cita.getNotas(),
                cita.getAntecedentes()
        );
    }
}
