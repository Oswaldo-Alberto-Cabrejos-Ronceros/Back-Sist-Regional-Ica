package com.clinicaregional.clinica.mapper;

import org.springframework.stereotype.Component;

import com.clinicaregional.clinica.dto.request.MedicoEspecialidadRequest;
import com.clinicaregional.clinica.dto.response.MedicoEspecialidadResponse;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Especialidad;
import com.clinicaregional.clinica.entity.MedicoEspecialidad;
import com.clinicaregional.clinica.entity.MedicoEspecialidadId;

@Component
public class MedicoEspecialidadMapper {

    // De Request a Entidad
    public static MedicoEspecialidad toEntity(MedicoEspecialidadRequest request, Medico medico,
            Especialidad especialidad) {
        MedicoEspecialidad entity = new MedicoEspecialidad();

        // Construir el ID compuesto
        MedicoEspecialidadId id = new MedicoEspecialidadId(medico.getId(), especialidad.getId());
        entity.setId(id); 

        entity.setMedico(medico);
        entity.setEspecialidad(especialidad);
        entity.setDesdeFecha(request.getDesdeFecha());
        return entity;
    }

    // De Entidad a Response
    public static MedicoEspecialidadResponse toResponse(MedicoEspecialidad entity) {
        MedicoEspecialidadResponse response = new MedicoEspecialidadResponse();
        response.setMedicoId(entity.getMedico().getId());
        response.setNombreMedico(entity.getMedico().getNombres() + " " + entity.getMedico().getApellidos()); // Concatenar
                                                                                                             // nombres
                                                                                                             // y
                                                                                                             // apellidos
        response.setEspecialidadId(entity.getEspecialidad().getId());
        response.setNombreEspecialidad(entity.getEspecialidad().getNombre()); // Igual aquí
        response.setDesdeFecha(entity.getDesdeFecha());
        return response;
    }
}