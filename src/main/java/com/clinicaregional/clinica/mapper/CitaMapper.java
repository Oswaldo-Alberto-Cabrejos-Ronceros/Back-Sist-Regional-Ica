package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
import com.clinicaregional.clinica.entity.*;
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

        // Relaciones (solo seteamos el ID)
        Paciente paciente = new Paciente();
        paciente.setId(request.getPacienteId());
        cita.setPaciente(paciente);

        Medico medico = new Medico();
        medico.setId(request.getMedicoId());
        cita.setMedico(medico);

        Servicio servicio = new Servicio();
        servicio.setId(request.getServicioId());
        cita.setServicio(servicio);

        if (request.getSeguroId() != null) {
            Seguro seguro = new Seguro();
            seguro.setId(request.getSeguroId());
            cita.setSeguro(seguro);
        }
        if (request.getCoberturaId() != null) {
            Cobertura cobertura = new Cobertura();
            cobertura.setId(request.getCoberturaId());
            cita.setCobertura(cobertura);
        }
        return cita;
    }

    public CitaResponse toResponse(Cita cita) {
        return new CitaResponse(
                cita.getId(),
                cita.getFecha(),
                cita.getHora(),
                cita.getEstadoCita(),
                cita.getNotas(),
                cita.getAntecedentes(),
                cita.getPaciente().getId(),
                cita.getMedico().getId(),
                cita.getServicio().getId(),
                cita.getSeguro() != null ? cita.getSeguro().getId() : null,
                cita.getCobertura() != null ? cita.getCobertura().getId() : null);
    }
}
