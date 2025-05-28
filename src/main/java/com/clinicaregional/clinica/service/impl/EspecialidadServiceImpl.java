package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.request.EspecialidadRequest;
import com.clinicaregional.clinica.dto.response.EspecialidadResponse;
import com.clinicaregional.clinica.entity.Especialidad;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.EspecialidadMapper;
import com.clinicaregional.clinica.repository.EspecialidadRepository;
import com.clinicaregional.clinica.service.EspecialidadService;
import com.clinicaregional.clinica.util.FiltroEstado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository especialidadRepository;
    private final EspecialidadMapper especialidadMapper;
    private final FiltroEstado filtroEstado;
    

    @Transactional(readOnly = true)
    @Override
    public List<EspecialidadResponse> listarEspecialidades() {
        filtroEstado.activarFiltroEstado(true);
        return especialidadRepository.findAll()
                .stream()
                .map(especialidadMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<EspecialidadResponse> getEspecialidadById(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Especialidad especialidad = especialidadRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una especialidad con el id " + id));
        return Optional.of(especialidadMapper.toResponse(especialidad));
    }

    @Transactional
    @Override
    public EspecialidadResponse guardarEspecialidad(EspecialidadRequest especialidadRequest) {
        filtroEstado.activarFiltroEstado(true);
        if (especialidadRepository.existsByNombre(especialidadRequest.getNombre())) {
            throw new DuplicateResourceException("Ya existe una especialidad con el nombre ingresado");
        }
        Especialidad especialidad = especialidadMapper.toEntity(especialidadRequest);
        Especialidad savedEspecialidad = especialidadRepository.save(especialidad);
        return especialidadMapper.toResponse(savedEspecialidad);
    }

    @Transactional
    @Override
    public EspecialidadResponse actualizarEspecialidad(Long id, EspecialidadRequest especialidadRequest) {
        filtroEstado.activarFiltroEstado(true);
        Especialidad especialidad = especialidadRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada con ID: " + id));
        if (!especialidad.getNombre().equalsIgnoreCase(especialidadRequest.getNombre()) &&
                especialidadRepository.existsByNombre(especialidadRequest.getNombre())) {
            throw new DuplicateResourceException("Ya existe una especialidad con el nombre ingresado");
        }
        especialidad.setNombre(especialidadRequest.getNombre());
        especialidad.setDescripcion(especialidadRequest.getDescripcion());
        especialidad.setImagen(especialidadRequest.getImagen());

        Especialidad updatedEspecialidad = especialidadRepository.save(especialidad);
        return especialidadMapper.toResponse(updatedEspecialidad);
    }

    @Transactional
    @Override
    public void eliminarEspecialidad(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Especialidad especialidad = especialidadRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));
        especialidad.setEstado(false); // borrado lógico
        especialidadRepository.save(especialidad);
    }
}
