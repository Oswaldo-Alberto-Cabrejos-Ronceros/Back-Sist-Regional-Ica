package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.ServicioRequest;
import com.clinicaregional.clinica.dto.response.ServicioResponse;
import com.clinicaregional.clinica.entity.Servicio;

import java.util.List;
import java.util.Optional;

public interface ServicioService {

    ServicioResponse agregarServicio(ServicioRequest servicioRequest);

    void eliminarServicio(Long id);

    ServicioResponse actualizarServicio(Long id, ServicioRequest servicioRequest);

    List<Servicio> listarTodosActivos();

    Optional<Servicio> buscarPorIdActivo(Long id);
}
