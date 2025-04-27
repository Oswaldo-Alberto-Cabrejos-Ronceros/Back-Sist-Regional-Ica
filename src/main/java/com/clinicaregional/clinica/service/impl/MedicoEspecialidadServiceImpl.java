package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.request.MedicoEspecialidadRequest;
import com.clinicaregional.clinica.dto.response.MedicoEspecialidadResponse;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Especialidad;
import com.clinicaregional.clinica.entity.MedicoEspecialidad;
import com.clinicaregional.clinica.entity.MedicoEspecialidadId;
import com.clinicaregional.clinica.mapper.MedicoEspecialidadMapper;
import com.clinicaregional.clinica.repository.MedicoEspecialidadRepository;
import com.clinicaregional.clinica.repository.MedicoRepository;
import com.clinicaregional.clinica.repository.EspecialidadRepository;
import com.clinicaregional.clinica.service.MedicoEspecialidadService;
import com.clinicaregional.clinica.util.FiltroEstado;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicoEspecialidadServiceImpl extends FiltroEstado implements MedicoEspecialidadService {

    private final MedicoEspecialidadRepository medicoEspecialidadRepository;
    private final MedicoRepository medicoRepository;
    private final EspecialidadRepository especialidadRepository;

    @Override
    public MedicoEspecialidadResponse registrarRelacionME(MedicoEspecialidadRequest request) {
        activarFiltroEstado(true);
        // Cargar entidades Medico y Especialidad
        Medico medico = medicoRepository.findByIdAndEstadoIsTrue(request.getMedicoId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Médico no encontrado con ID: " + request.getMedicoId()));
        Especialidad especialidad = especialidadRepository.findByIdAndEstadoIsTrue(request.getEspecialidadId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Especialidad no encontrada con ID: " + request.getEspecialidadId()));
        if(medicoEspecialidadRepository.existsByMedicoAndEspecialidad(medico, especialidad)){
            throw new EntityExistsException("Ya existe esta relacion");
        }
        // Mapear y guardar
        MedicoEspecialidad entity = MedicoEspecialidadMapper.toEntity(request, medico, especialidad);
        MedicoEspecialidad saved = medicoEspecialidadRepository.save(entity);
        return MedicoEspecialidadMapper.toResponse(saved);
    }

    @Override
    public MedicoEspecialidadResponse actualizarRelacionME(Long medicoId, Long especialidadId,
            MedicoEspecialidadRequest request) {
        activarFiltroEstado(true);
        // Usamos el EmbeddedId compuesto para la búsqueda
        MedicoEspecialidadId id = new MedicoEspecialidadId(medicoId, especialidadId);

        // Buscamos la entidad usando la clave compuesta
        MedicoEspecialidad entity = medicoEspecialidadRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new RuntimeException("Relación no encontrada"));

        // Actualizamos los campos necesarios de la entidad
        entity.setDesdeFecha(request.getDesdeFecha());

        // Si deseas cambiar de médico o especialidad, tendrías que actualizar esos
        // objetos también.
        // Por ejemplo:
        // entity.setMedico(nuevoMedico);
        // entity.setEspecialidad(nuevaEspecialidad);

        // Guardamos los cambios en la base de datos
        MedicoEspecialidad updatedEntity = medicoEspecialidadRepository.save(entity);

        // Retornamos la respuesta usando el mapper
        return MedicoEspecialidadMapper.toResponse(updatedEntity);
    }

    @Override
    public void eliminarRelacionME(Long medicoId, Long especialidadId) {
        activarFiltroEstado(true);
        MedicoEspecialidadId id = new MedicoEspecialidadId(medicoId, especialidadId);
        MedicoEspecialidad medicoEspecialidad = medicoEspecialidadRepository.findByIdAndEstadoIsTrue(id).orElseThrow(()->new RuntimeException("Relación Médico-Especialidad no encontrada para eliminación"));
        medicoEspecialidad.setEstado(false);
        medicoEspecialidadRepository.save(medicoEspecialidad);
    }

    @Override
    public List<MedicoEspecialidadResponse> obtenerEspecialidadDelMedico(Long medicoId) {
        activarFiltroEstado(true);
        List<MedicoEspecialidad> relaciones = medicoEspecialidadRepository.findByMedicoId(medicoId);
        return relaciones.stream()
                .map(MedicoEspecialidadMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicoEspecialidadResponse> obtenerMedicosPorEspecialidad(Long especialidadId) {
        activarFiltroEstado(true);
        List<MedicoEspecialidad> relaciones = medicoEspecialidadRepository.findByEspecialidadId(especialidadId);
        return relaciones.stream()
                .map(MedicoEspecialidadMapper::toResponse)
                .collect(Collectors.toList());
    }
}
