package com.clinicaregional.clinica.mapper;

import org.springframework.stereotype.Component;

import com.clinicaregional.clinica.dto.request.EspecialidadRequest;
import com.clinicaregional.clinica.dto.response.EspecialidadResponse;
import com.clinicaregional.clinica.entity.Especialidad;

@Component
public class EspecialidadMapper {
    
    public static Especialidad toEntity(EspecialidadRequest request) {
        Especialidad especialidad = new Especialidad();
        especialidad.setNombre(request.getNombre());
        especialidad.setDescripcion(request.getDescripcion());
        especialidad.setImagen(request.getImagen());
        return especialidad;
    }

    public static EspecialidadResponse toResponse(Especialidad especialidad) {
        EspecialidadResponse response = new EspecialidadResponse();
        response.setId(especialidad.getId());
        response.setNombre(especialidad.getNombre());
        response.setDescripcion(especialidad.getDescripcion());
        response.setImagen(especialidad.getImagen());
        return response;
    }
}