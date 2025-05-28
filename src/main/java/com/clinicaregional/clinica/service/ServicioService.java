package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.ServicioRequest;
import com.clinicaregional.clinica.dto.response.ServicioResponse;
import java.util.List;
public interface ServicioService {

    ServicioResponse agregarServicio(ServicioRequest servicioRequest);

    void eliminarServicio(Long id);

    ServicioResponse actualizarServicio(Long id, ServicioRequest servicioRequest);
    
}
