package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.ServicioRequest;
import com.clinicaregional.clinica.dto.response.ServicioResponse;
import com.clinicaregional.clinica.entity.Servicio;

import java.util.List;
public interface ServicioService {

    List<ServicioResponse> obtenerServicios();

    ServicioResponse agregarServicio(ServicioRequest servicioRequest);

    void eliminarServicio(Long id);

    ServicioResponse actualizarServicio(Long id, ServicioRequest servicioRequest);
    
}
