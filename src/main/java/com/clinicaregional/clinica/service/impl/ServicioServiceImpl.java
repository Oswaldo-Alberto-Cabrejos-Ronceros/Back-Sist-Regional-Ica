package com.clinicaregional.clinica.service.impl;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import com.clinicaregional.clinica.dto.request.ServicioRequest;
import com.clinicaregional.clinica.dto.response.ServicioResponse;
import com.clinicaregional.clinica.entity.Servicio;
import com.clinicaregional.clinica.mapper.ServicioMapper;
import com.clinicaregional.clinica.repository.ServicioRepository;
import com.clinicaregional.clinica.service.ServicioService;
import com.clinicaregional.clinica.util.FiltroEstado;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;



@Service
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository servicioRepository;
    private final ServicioMapper servicioMapper;
    private final FiltroEstado filtroEstado;

    public ServicioServiceImpl(ServicioRepository servicioRepository, ServicioMapper servicioMapper, FiltroEstado filtroEstado) {
        this.servicioRepository = servicioRepository;
        this.servicioMapper = servicioMapper;
        this.filtroEstado = filtroEstado;
    }

    @Transactional
    @Override
    public ServicioResponse agregarServicio(ServicioRequest servicioRequest) {
        filtroEstado.activarFiltroEstado(true);
        if(servicioRepository.existsByNombre(servicioRequest.getNombre())) {
            throw new DuplicateResourceException("Ya existe un servicio con el nombre ingresado");
        }
        Servicio servicio = servicioMapper.mapToServicio(servicioRequest);
        Servicio savedServicio = servicioRepository.save(servicio);
        return servicioMapper.mapToServicioResponse(savedServicio);
    }

    @Transactional
    @Override
    public void eliminarServicio(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Servicio servicio = servicioRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));
        servicio.setEstado(false);
        servicioRepository.save(servicio);
    }

    @Transactional
    @Override
    public ServicioResponse actualizarServicio(Long id, ServicioRequest servicioRequest) {
        filtroEstado.activarFiltroEstado(true);
        Servicio servicio = servicioRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));
        if(servicioRepository.existsByNombre(servicioRequest.getNombre())) {
            throw new DuplicateResourceException("Ya existe un servicio con el nombre ingresado");
        }
        servicio.setNombre(servicioRequest.getNombre());
        servicio.setDescripcion(servicioRequest.getDescripcion());
        servicio.setImagenUrl(servicioRequest.getImagenUrl());
        Servicio updatedServicio = servicioRepository.save(servicio);
        return servicioMapper.mapToServicioResponse(updatedServicio);
    }
    
}
