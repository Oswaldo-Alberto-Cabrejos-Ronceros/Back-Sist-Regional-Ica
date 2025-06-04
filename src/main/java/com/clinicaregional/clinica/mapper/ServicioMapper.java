package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.entity.Especialidad;
import org.springframework.stereotype.Component;

import com.clinicaregional.clinica.dto.request.ServicioRequest;
import com.clinicaregional.clinica.dto.response.ServicioResponse;
import com.clinicaregional.clinica.entity.Servicio;

@Component
public class ServicioMapper {

    public Servicio mapToServicio(ServicioRequest dto) {
        Especialidad especialidad = new Especialidad();
        especialidad.setId(dto.getEspecialidadId());
        Servicio servicio = new Servicio();
        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setImagenUrl(dto.getImagenUrl());
        servicio.setPrice(dto.getPrice());
        servicio.setEspecialidad(especialidad);
        return servicio;
    }

    public ServicioResponse mapToServicioResponse(Servicio servicio) {
        ServicioResponse response = new ServicioResponse();
        response.setId(servicio.getId());
        response.setNombre(servicio.getNombre());
        response.setDescripcion(servicio.getDescripcion());
        response.setImagenUrl(servicio.getImagenUrl());
        response.setPrice(servicio.getPrice());
        response.setEspecialidadId(servicio.getEspecialidad().getId());
        return response;
    }

}
