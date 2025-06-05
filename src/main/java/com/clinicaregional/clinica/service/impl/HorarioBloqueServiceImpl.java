package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.request.HorarioBloqueRequest;
import com.clinicaregional.clinica.dto.response.HorarioBloqueResponse;
import com.clinicaregional.clinica.dto.response.MedicoEspecialidadResponse;
import com.clinicaregional.clinica.entity.Disponibilidad;
import com.clinicaregional.clinica.entity.HorarioBloque;
import com.clinicaregional.clinica.enums.EstadoBloque;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.HorarioBloqueMapper;
import com.clinicaregional.clinica.repository.DisponibilidadRepository;
import com.clinicaregional.clinica.repository.HorarioBloqueRepository;
import com.clinicaregional.clinica.service.EspecialidadService;
import com.clinicaregional.clinica.service.HorarioBloqueService;
import com.clinicaregional.clinica.service.MedicoEspecialidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HorarioBloqueServiceImpl implements HorarioBloqueService {

    private final HorarioBloqueRepository horarioBloqueRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final HorarioBloqueMapper horarioBloqueMapper;
    private final MedicoEspecialidadService medicoEspecialidadService;
    private final EspecialidadService especialidadService;

    @Transactional(readOnly = true)
    @Override
    public HorarioBloqueResponse obtenerPorId(Long id) {
        HorarioBloque bloque = horarioBloqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario bloque no encontrado con ID: " + id));
        return horarioBloqueMapper.mapToHorarioBloqueResponse(bloque);
    }

    @Transactional(readOnly = true)
    @Override
    public List<HorarioBloqueResponse> listarPorDisponibilidad(Long disponibilidadId) {
        return horarioBloqueRepository.findByDisponibilidadId(disponibilidadId).stream()
                .map(horarioBloqueMapper::mapToHorarioBloqueResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<HorarioBloqueResponse> listarPorMedico(Long medicoId) {
        return horarioBloqueRepository.findByDisponibilidad_Medico_Id(medicoId).stream()
                .map(horarioBloqueMapper::mapToHorarioBloqueResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<HorarioBloqueResponse> listarPorFecha(LocalDate fecha) {
        return horarioBloqueRepository.findByFecha(fecha).stream()
                .map(horarioBloqueMapper::mapToHorarioBloqueResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<HorarioBloqueResponse> listarPorEspecialidad(Long especialidadId) {
        //verificamos si existe la especialidad
        especialidadService.getEspecialidadById(especialidadId);
        //obtenemos los medicos de una especialidad
        List<MedicoEspecialidadResponse> medicos = medicoEspecialidadService.obtenerMedicosPorEspecialidad(especialidadId);
        List<HorarioBloqueResponse> horariosBloques=new ArrayList<>();
        for (MedicoEspecialidadResponse medico : medicos){
            List<HorarioBloqueResponse> horarioBloquesPorMedico= listarPorMedico(medico.getMedicoId());
            horariosBloques.addAll(horarioBloquesPorMedico);
        }
        return horariosBloques;
    }

    @Transactional(readOnly = true)
    @Override
    public HorarioBloqueResponse actualizarEstado(Long id, String nuevoEstado) {
        HorarioBloque bloque = horarioBloqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario bloque no encontrado con ID: " + id));

        EstadoBloque estado = EstadoBloque.valueOf(nuevoEstado.toUpperCase());
        bloque.setEstadoBloque(estado);

        HorarioBloque actualizado = horarioBloqueRepository.save(bloque);
        return horarioBloqueMapper.mapToHorarioBloqueResponse(actualizado);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean estaDisponible(Long id) {
        HorarioBloque bloque = horarioBloqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario bloque no encontrado con ID: " + id));
        return bloque.getEstadoBloque() == EstadoBloque.DISPONIBLE;
    }

    @Transactional
    @Override
    public void liberar(Long id) {
        HorarioBloque bloque = horarioBloqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario bloque no encontrado con ID: " + id));

        if (bloque.getEstadoBloque() == EstadoBloque.DISPONIBLE) {
            throw new DuplicateResourceException("El bloque ya está libre.");
        }

        bloque.setEstadoBloque(EstadoBloque.DISPONIBLE);
        horarioBloqueRepository.save(bloque);
    }

    @Override
    public HorarioBloqueResponse registrar(HorarioBloqueRequest request) {
        Disponibilidad disponibilidad = disponibilidadRepository.findById(request.getDisponibilidadId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Disponibilidad no encontrada con ID: " + request.getDisponibilidadId()));

        HorarioBloque bloque = horarioBloqueMapper.mapToHorarioBloque(request);
        bloque.setDisponibilidad(disponibilidad);

        HorarioBloque guardado = horarioBloqueRepository.save(bloque);
        return horarioBloqueMapper.mapToHorarioBloqueResponse(guardado);
    }
}
