package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.EspecialidadRequest;
import com.clinicaregional.clinica.dto.EspecialidadResponse;
import com.clinicaregional.clinica.entity.Especialidad;
import com.clinicaregional.clinica.mapper.EspecialidadMapper;
import com.clinicaregional.clinica.repository.EspecialidadRepository;
import com.clinicaregional.clinica.service.EspecialidadService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository especialidadRepository;

    @Override
    @Transactional
    public EspecialidadResponse guardarEspecialidad(EspecialidadRequest especialidadRequest) {
        Especialidad especialidad = EspecialidadMapper.toEntity(especialidadRequest);
        Especialidad savedEspecialidad = especialidadRepository.save(especialidad);
        return EspecialidadMapper.toResponse(savedEspecialidad);
    }

    @Override
    @Transactional
    public EspecialidadResponse actualizarEspecialidad(Long id, EspecialidadRequest especialidadRequest) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada con ID: " + id));
        
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
        if (!especialidadRepository.existsById(id)) {
            throw new EntityNotFoundException("Especialidad no encontrada con ID: " + id);
        }
        especialidadRepository.deleteById(id);
    }
}