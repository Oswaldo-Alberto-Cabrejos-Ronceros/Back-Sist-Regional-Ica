package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.request.HistorialClinicoRequest;
import com.clinicaregional.clinica.dto.response.HistorialClinicoResponse;
import com.clinicaregional.clinica.entity.HistorialClinico;
import com.clinicaregional.clinica.entity.Paciente;
import org.springframework.stereotype.Component;

@Component
public class HistorialClinicoMapper {

    public HistorialClinico toEntity(HistorialClinicoRequest dto, Paciente paciente) {
        return HistorialClinico.builder()
                .paciente(paciente)
                .fechaRegistro(dto.getFechaRegistro())
                .build();
    }

    public HistorialClinicoResponse toResponse(HistorialClinico entity) {
        return HistorialClinicoResponse.builder()
                .id(entity.getId())
                .pacienteId(entity.getPaciente().getId())
                .fechaRegistro(entity.getFechaRegistro())
                .build();
    }
}
