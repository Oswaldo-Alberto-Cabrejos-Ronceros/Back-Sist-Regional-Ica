package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.entity.Especialidad;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.service.EspecialidadService;
import com.clinicaregional.clinica.service.S3ServicePublic;
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
    private final S3ServicePublic s3Service;

    public ServicioServiceImpl(ServicioRepository servicioRepository, ServicioMapper servicioMapper,
                               FiltroEstado filtroEstado, EspecialidadService especialidadService, S3ServicePublic s3Service) {
        this.servicioRepository = servicioRepository;
        this.servicioMapper = servicioMapper;
        this.filtroEstado = filtroEstado;
        this.especialidadService = especialidadService;
        this.s3Service = s3Service;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ServicioResponse> obtenerServicios() {
        filtroEstado.activarFiltroEstado(true);
        List<ServicioResponse> servicios = servicioRepository.findAll().stream()
                .map(servicioMapper::mapToServicioResponse)
                .toList();
        return servicios.stream()
                .map(this::agregarUrlImage)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ServicioResponse> obtenerServiciosPorEspecialidadId(Long especialidadId) {
        filtroEstado.activarFiltroEstado(true);
        especialidadService.getEspecialidadById(especialidadId).orElseThrow(() -> new ResourceNotFoundException("No se encontro especialidad con el id:" + especialidadId));
        List<ServicioResponse> servicios = servicioRepository.findAllByEspecialidad_Id(especialidadId).stream()
                .map(servicioMapper::mapToServicioResponse)
                .toList();
        return servicios.stream().map(this::agregarUrlImage).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ServicioResponse agregarServicio(ServicioRequest servicioRequest, MultipartFile archivo) {
        filtroEstado.activarFiltroEstado(true);
        if (servicioRepository.existsByNombre(servicioRequest.getNombre())) {
            throw new DuplicateResourceException("Ya existe un servicio con el nombre ingresado");
        }
        especialidadService.getEspecialidadById(servicioRequest.getEspecialidadId()).orElseThrow(() -> new ResourceNotFoundException("No se encontro especialidad con el id:" + servicioRequest.getEspecialidadId()));
        Servicio servicio = servicioMapper.mapToServicio(servicioRequest);
        if(archivo!=null){
            String key = s3Service.subirArchivo(archivo, "servicios" + servicioRequest.getNombre());
            servicio.setImagenUrl(key);
        }
        Servicio savedServicio = servicioRepository.save(servicio);
        ServicioResponse servicioResponse = servicioMapper.mapToServicioResponse(savedServicio);

        return this.agregarUrlImage(servicioResponse);
    }

    @Transactional
    @Override
    public void eliminarServicio(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Servicio servicio = servicioRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));
        servicio.setEstado(false);
        if(servicio.getImagenUrl()!=null){
            s3Service.eliminarArchivo(servicio.getImagenUrl());
        }
        servicioRepository.save(servicio);
    }

    @Transactional
    @Override
    public ServicioResponse actualizarServicio(Long id, ServicioRequest servicioRequest, MultipartFile archivo) {
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
        if(archivo!=null){
            if(servicio.getImagenUrl()!=null){
                s3Service.eliminarArchivo(servicio.getImagenUrl());
            }
            String key = s3Service.subirArchivo(archivo, "servicios" + servicioRequest.getNombre());
            servicio.setImagenUrl(key);
        }
        Servicio updatedServicio = servicioRepository.save(servicio);
        ServicioResponse response = servicioMapper.mapToServicioResponse(updatedServicio);
        return this.agregarUrlImage(response);
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

        List<ServicioResponse> contenidoWithUrlImage = contenido.stream().map(this::agregarUrlImage).toList();

        return new PagedResponse<>(
                contenido,
                servicioPage.getNumber(),
                servicioPage.getSize(),
                servicioPage.getTotalElements(),
                servicioPage.getTotalPages(),
                servicioPage.isLast()
        );
    }


    //funcion para obtener el url
    private ServicioResponse agregarUrlImage(ServicioResponse servicioResponse) {
        if(servicioResponse.getImagenUrl()!=null){
            String imageUrl = s3Service.generarUrlPublico(servicioResponse.getImagenUrl());
            servicioResponse.setImagenUrl(imageUrl);
        }
        return servicioResponse;
    }
}
