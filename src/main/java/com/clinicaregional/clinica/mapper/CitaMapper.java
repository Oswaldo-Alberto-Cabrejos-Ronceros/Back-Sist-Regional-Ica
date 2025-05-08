package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
import com.clinicaregional.clinica.entity.*;
import org.springframework.stereotype.Component;

@Component
public class CitaMapper {

    //de requesta a Entidad
    public Cita toEntity(CitaRequest citaRequest) {
        //paciente
        Paciente paciente = new Paciente();
        paciente.setId(citaRequest.getPacienteId());
        //medico
        Medico medico = new Medico();
        medico.setId(citaRequest.getMedicoId());
        //servicio
        Servicio servicio = new Servicio();
        servicio.setId(citaRequest.getServicioId());
        return Cita.builder().fechaHora(citaRequest.getFechaHora())
                .estadoCita(citaRequest.getEstadoCita())
                .notas(citaRequest.getNotas())
                .antecedentes(citaRequest.getAntecedentes()).paciente(paciente).medico(medico)
                .servicio(servicio).build();
    }

    //de entidad a Response
    public CitaResponse toResponse(Cita cita) {
        CitaResponse citaResponse = null;
        if(cita.getSeguro()!=null&&cita.getCobertura()!=null){
            citaResponse= new CitaResponse(cita.getId(),cita.getFechaHora().toLocalDate(),cita.getFechaHora().toLocalTime(),
                    cita.getEstadoCita(),cita.getNotas(),cita.getAntecedentes(),cita.getPaciente().getId(),
                    cita.getMedico().getId(),cita.getServicio().getId(),cita.getSeguro().getId(),cita.getCobertura().getId());
        } else{
            citaResponse=CitaResponse.builder().id(cita.getId()).fecha(cita.getFechaHora().toLocalDate()).hora(cita.getFechaHora().toLocalTime()).estadoCita(cita.getEstadoCita()).notas(cita.getNotas())
                    .antecedentes(cita.getAntecedentes()).pacienteId(cita.getPaciente().getId()).medicoId(cita.getMedico().getId()).servicioId(cita.getServicio().getId()).build();
        }
        return citaResponse;
    }
}
