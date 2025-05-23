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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicoEspecialidadServiceImpl implements MedicoEspecialidadService {

    private final MedicoEspecialidadRepository medicoEspecialidadRepository;
    private final MedicoRepository medicoRepository;
    private final EspecialidadRepository especialidadRepository;
    private final FiltroEstado filtroEstado;

    @Override
    @Transactional
    public MedicoEspecialidadResponse registrarRelacionME(MedicoEspecialidadRequest request) {
        filtroEstado.activarFiltroEstado(true);

        Medico medico = medicoRepository.findByIdAndEstadoIsTrue(request.getMedicoId())
                .orElseThrow(() -> new EntityNotFoundException("Médico no encontrado con ID: " + request.getMedicoId()));
        Especialidad especialidad = especialidadRepository.findByIdAndEstadoIsTrue(request.getEspecialidadId())
                .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada con ID: " + request.getEspecialidadId()));

        if (medicoEspecialidadRepository.existsByMedicoAndEspecialidad(medico, especialidad)) {
            throw new EntityExistsException("Ya existe esta relación");
        }

        MedicoEspecialidad entity = MedicoEspecialidadMapper.toEntity(request, medico, especialidad);
        MedicoEspecialidad saved = medicoEspecialidadRepository.save(entity);
        return MedicoEspecialidadMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public MedicoEspecialidadResponse actualizarRelacionME(Long medicoId, Long especialidadId, MedicoEspecialidadRequest request) {
        filtroEstado.activarFiltroEstado(true);

        MedicoEspecialidadId id = new MedicoEspecialidadId(medicoId, especialidadId);
        MedicoEspecialidad entity = medicoEspecialidadRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new RuntimeException("Relación no encontrada"));

        entity.setDesdeFecha(request.getDesdeFecha());

        MedicoEspecialidad updatedEntity = medicoEspecialidadRepository.save(entity);
        return MedicoEspecialidadMapper.toResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void eliminarRelacionME(Long medicoId, Long especialidadId) {
        filtroEstado.activarFiltroEstado(true);

        MedicoEspecialidadId id = new MedicoEspecialidadId(medicoId, especialidadId);
        MedicoEspecialidad medicoEspecialidad = medicoEspecialidadRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new RuntimeException("Relación Médico-Especialidad no encontrada para eliminación"));

        medicoEspecialidad.setEstado(false);
        medicoEspecialidadRepository.save(medicoEspecialidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicoEspecialidadResponse> obtenerEspecialidadDelMedico(Long medicoId) {
        filtroEstado.activarFiltroEstado(true);

        List<MedicoEspecialidad> relaciones = medicoEspecialidadRepository.findByMedicoId(medicoId);
        return relaciones.stream()
                .map(MedicoEspecialidadMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicoEspecialidadResponse> obtenerMedicosPorEspecialidad(Long especialidadId) {
        filtroEstado.activarFiltroEstado(true);

        List<MedicoEspecialidad> relaciones = medicoEspecialidadRepository.findByEspecialidadId(especialidadId);
        return relaciones.stream()
                .map(MedicoEspecialidadMapper::toResponse)
                .collect(Collectors.toList());
    }
}
