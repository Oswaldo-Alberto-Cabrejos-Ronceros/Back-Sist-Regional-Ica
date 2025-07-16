package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.entity.Especialidad;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.service.EspecialidadService;
import org.springframework.stereotype.Service;
import com.clinicaregional.clinica.dto.request.ServicioRequest;
import com.clinicaregional.clinica.dto.response.ServicioResponse;
import com.clinicaregional.clinica.entity.Servicio;
import com.clinicaregional.clinica.mapper.ServicioMapper;
import com.clinicaregional.clinica.repository.ServicioRepository;
import com.clinicaregional.clinica.service.ServicioService;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import com.clinicaregional.clinica.dto.response.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository servicioRepository;
    private final ServicioMapper servicioMapper;
    private final FiltroEstado filtroEstado;
    private final EspecialidadService especialidadService;

    public ServicioServiceImpl(ServicioRepository servicioRepository, ServicioMapper servicioMapper,
            FiltroEstado filtroEstado, EspecialidadService especialidadService) {
        this.servicioRepository = servicioRepository;
        this.servicioMapper = servicioMapper;
        this.filtroEstado = filtroEstado;
        this.especialidadService = especialidadService;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ServicioResponse> obtenerServicios() {
        filtroEstado.activarFiltroEstado(true);
        return servicioRepository.findAll().stream()
                .map(servicioMapper::mapToServicioResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ServicioResponse> obtenerServiciosPorEspecialidadId(Long especialidadId) {
        filtroEstado.activarFiltroEstado(true);
        especialidadService.getEspecialidadById(especialidadId).orElseThrow(
                () -> new ResourceNotFoundException("No se encontro especialidad con el id:" + especialidadId));
        return servicioRepository.findAllByEspecialidad_Id(especialidadId).stream()
                .map(servicioMapper::mapToServicioResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ServicioResponse agregarServicio(ServicioRequest servicioRequest) {
        filtroEstado.activarFiltroEstado(true);
        if (servicioRepository.existsByNombre(servicioRequest.getNombre())) {
            throw new DuplicateResourceException("Ya existe un servicio con el nombre ingresado");
        }
        especialidadService.getEspecialidadById(servicioRequest.getEspecialidadId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro especialidad con el id:" + servicioRequest.getEspecialidadId()));
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
        if (servicioRepository.existsByNombre(servicioRequest.getNombre())) {
            throw new DuplicateResourceException("Ya existe un servicio con el nombre ingresado");
        }
        especialidadService.getEspecialidadById(servicioRequest.getEspecialidadId()).orElseThrow(() -> new ResourceNotFoundException("No se encontro especialidad con el id:" + servicioRequest.getEspecialidadId()));
        servicio.setNombre(servicioRequest.getNombre());
        servicio.setDescripcion(servicioRequest.getDescripcion());
        servicio.setImagenUrl(servicioRequest.getImagenUrl());
        Especialidad especialidad = new Especialidad();
        especialidad.setId(servicioRequest.getEspecialidadId());
        servicio.setEspecialidad(especialidad);
        Servicio updatedServicio = servicioRepository.save(servicio);
        return servicioMapper.mapToServicioResponse(updatedServicio);
    }

   
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ServicioResponse> obtenerServiciosPaginado(Pageable pageable) {
        filtroEstado.activarFiltroEstado(true);
        Page<Servicio> servicioPage = servicioRepository.findAllByEstadoIsTrue(pageable);

        List<ServicioResponse> contenido = servicioPage.getContent()
                .stream()
                .map(servicioMapper::mapToServicioResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                contenido,
                servicioPage.getNumber(),
                servicioPage.getSize(),
                servicioPage.getTotalElements(),
                servicioPage.getTotalPages(),
                servicioPage.isLast()
        );
    }

}
