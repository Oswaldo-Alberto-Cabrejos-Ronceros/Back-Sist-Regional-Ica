package com.clinicaregional.clinica.mapper;

import org.springframework.stereotype.Component;

import com.clinicaregional.clinica.dto.request.ServicioRequest;
import com.clinicaregional.clinica.dto.response.ServicioResponse;
import com.clinicaregional.clinica.entity.Servicio;

@Component
public class ServicioMapper {

    public Servicio mapToServicio(ServicioRequest dto) {
        Servicio servicio = new Servicio();
        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setImagenUrl(dto.getImagenUrl());
        return servicio;
    }

    public ServicioResponse mapToServicioResponse(Servicio servicio) {
        ServicioResponse response = new ServicioResponse();
        response.setId(servicio.getId());
        response.setNombre(servicio.getNombre());
        response.setDescripcion(servicio.getDescripcion());
        response.setImagenUrl(servicio.getImagenUrl());
        return response;
    }

}
