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
        //seguro
        Seguro seguro = new Seguro();
        //cobertura
        Cobertura cobertura = new Cobertura();
        if(citaRequest.getSeguroId()!=null&&citaRequest.getCoberturaId()!=null){
            seguro.setId(citaRequest.getSeguroId());
            cobertura.setId(citaRequest.getCoberturaId());
        }
        return Cita.builder().fecha(citaRequest.getFecha())
                .hora(citaRequest.getHora())
                .estadoCita(citaRequest.getEstadoCita())
                .notas(citaRequest.getNotas())
                .antecedentes(citaRequest.getAntecedentes()).paciente(paciente).medico(medico)
                .servicio(servicio).seguro(seguro).cobertura(cobertura).build();
    }

    //de entidad a Response
    public CitaResponse toResponse(Cita cita) {
        return new CitaResponse(cita.getId(),cita.getFecha(),cita.getHora(),
                cita.getEstadoCita(),cita.getNotas(),cita.getAntecedentes(),cita.getPaciente().getId(),
                cita.getMedico().getId(),cita.getServicio().getId(),cita.getSeguro().getId(),cita.getCobertura().getId());
    }
}
