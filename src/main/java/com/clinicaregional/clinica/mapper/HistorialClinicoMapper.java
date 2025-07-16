package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.request.HistorialClinicoRequest;
import com.clinicaregional.clinica.dto.response.HistorialClinicoResponse;
import com.clinicaregional.clinica.entity.HistorialClinico;
import com.clinicaregional.clinica.entity.Paciente;
import org.springframework.stereotype.Component;

@Component
public class HistorialClinicoMapper {
    public HistorialClinico toEntity(HistorialClinicoRequest historialClinicoRequest) {
        Paciente paciente = new Paciente();
        paciente.setId(historialClinicoRequest.getPacienteId());
        return HistorialClinico.builder().paciente(paciente).build();
    }

    public HistorialClinicoResponse toResponse(HistorialClinico historialClinico) {
        return new HistorialClinicoResponse(historialClinico.getId(), historialClinico.getFecha(), historialClinico.getPaciente().getId());
    }
}
