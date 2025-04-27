package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.request.EspecialidadRequest;
import com.clinicaregional.clinica.dto.response.EspecialidadResponse;
import com.clinicaregional.clinica.entity.Especialidad;
import com.clinicaregional.clinica.mapper.EspecialidadMapper;
import com.clinicaregional.clinica.repository.EspecialidadRepository;
import com.clinicaregional.clinica.service.EspecialidadService;
import com.clinicaregional.clinica.util.FiltroEstado;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspecialidadServiceImpl extends FiltroEstado implements EspecialidadService {

    private final EspecialidadRepository especialidadRepository;
    private final EspecialidadMapper especialidadMapper;

    @Transactional(readOnly = true)
    @Override
    public List<EspecialidadResponse> listarEspecialidades() {
        activarFiltroEstado(true);
        return especialidadRepository.findAll().stream().map(EspecialidadMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<EspecialidadResponse> getEspecialidadById(Long id) {
        return especialidadRepository.findByIdAndEstadoIsTrue(id).map(EspecialidadMapper::toResponse);
    }

    @Override
    @Transactional
    public EspecialidadResponse guardarEspecialidad(EspecialidadRequest especialidadRequest) {
        activarFiltroEstado(true);
        if(especialidadRepository.existsByNombre(especialidadRequest.getNombre())){
            throw new RuntimeException("Ya existe una especialidad con el nombre ingresado");
        }
        Especialidad especialidad = EspecialidadMapper.toEntity(especialidadRequest);
        Especialidad savedEspecialidad = especialidadRepository.save(especialidad);
        return EspecialidadMapper.toResponse(savedEspecialidad);
    }

    @Override
    @Transactional
    public EspecialidadResponse actualizarEspecialidad(Long id, EspecialidadRequest especialidadRequest) {
        activarFiltroEstado(true);
        Especialidad especialidad = especialidadRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada con ID: " + id));
        if(especialidadRepository.existsByNombre(especialidadRequest.getNombre())){
            throw new RuntimeException("Ya existe una especialidad con el nombre ingresado");
        }
        // Actualizamos los campos manualmente
        especialidad.setNombre(especialidadRequest.getNombre());
        especialidad.setDescripcion(especialidadRequest.getDescripcion());
        especialidad.setImagen(especialidadRequest.getImagen());

        Especialidad updatedEspecialidad = especialidadRepository.save(especialidad);
        return EspecialidadMapper.toResponse(updatedEspecialidad);
    }

    @Override
    @Transactional
    public void eliminarEspecialidad(Long id) {
        activarFiltroEstado(true);
        Especialidad especialidad=especialidadRepository.findByIdAndEstadoIsTrue(id).orElseThrow(()->new EntityNotFoundException("Especialidad no encontrada"));
        especialidad.setEstado(false); //borrado logico
        especialidadRepository.save(especialidad);
    }
}