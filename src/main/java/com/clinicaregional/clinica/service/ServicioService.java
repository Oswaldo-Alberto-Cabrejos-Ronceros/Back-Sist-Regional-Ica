package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.ServicioRequest;
import com.clinicaregional.clinica.dto.response.ServicioResponse;
import com.clinicaregional.clinica.entity.Servicio;
import com.clinicaregional.clinica.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
public interface ServicioService {

    List<ServicioResponse> obtenerServicios();

    List<ServicioResponse> obtenerServiciosPorEspecialidadId(Long especialidadId);

    ServicioResponse agregarServicio(ServicioRequest servicioRequest, MultipartFile archivo);

    void eliminarServicio(Long id);

    ServicioResponse actualizarServicio(Long id, ServicioRequest servicioRequest, MultipartFile archivo);

    PagedResponse<ServicioResponse> obtenerServiciosPaginado(Pageable pageable);
    
}
