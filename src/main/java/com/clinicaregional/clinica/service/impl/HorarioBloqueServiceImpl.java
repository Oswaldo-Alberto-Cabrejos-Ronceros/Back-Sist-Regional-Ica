package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.request.HorarioBloqueRequest;
import com.clinicaregional.clinica.dto.response.HorarioBloqueResponse;
import com.clinicaregional.clinica.entity.HorarioBloque;
import com.clinicaregional.clinica.enums.EstadoBloque;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.HorarioBloqueMapper;
import com.clinicaregional.clinica.repository.CitaRepository;
import com.clinicaregional.clinica.repository.HorarioBloqueRepository;
import com.clinicaregional.clinica.service.DisponibilidadService;
import com.clinicaregional.clinica.service.HorarioBloqueService;
import com.clinicaregional.clinica.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HorarioBloqueServiceImpl implements HorarioBloqueService {

    private final HorarioBloqueRepository horarioBloqueRepository;
    private final HorarioBloqueMapper horarioBloqueMapper;
    private final MedicoService medicoService;
    private final CitaRepository citaRepository;
    private final DisponibilidadService disponibilidadService;

    @Autowired
    public HorarioBloqueServiceImpl(HorarioBloqueRepository horarioBloqueRepository, HorarioBloqueMapper horarioBloqueMapper,
                                    MedicoService medicoService, CitaRepository citaRepository, DisponibilidadService disponibilidadService) {
        this.horarioBloqueRepository = horarioBloqueRepository;
        this.horarioBloqueMapper = horarioBloqueMapper;
        this.medicoService = medicoService;
        this.citaRepository = citaRepository;
        this.disponibilidadService = disponibilidadService;
    }

    @Transactional(readOnly = true)
    @Override
    public List<HorarioBloqueResponse> obtenerHorariosBloques() {
        return horarioBloqueRepository.findAll().stream().map(horarioBloqueMapper::toResponse).collect((Collectors.toList()));
    }

    @Transactional(readOnly = true)
    @Override
    public List<HorarioBloqueResponse> obtenerHoraiosBloquesPorMedicoId(Long medicoId) {
        return horarioBloqueRepository.findAllByMedicoId(medicoId).stream().map(horarioBloqueMapper::toResponse).collect((Collectors.toList()));
    }

    @Transactional
    @Override
    public HorarioBloqueResponse crearHorarioBloque(HorarioBloqueRequest horarioBloqueRequest) {
        medicoService.obtenerMedicoPorId(horarioBloqueRequest.getMedicoId());
        if (!citaRepository.existsById(horarioBloqueRequest.getCitaId())) {
            throw new ResourceNotFoundException("No se encontro cita con el id: " + horarioBloqueRequest.getCitaId());
        }
        disponibilidadService.obtenerPorId(horarioBloqueRequest.getDisponibilidadId());
        HorarioBloque horarioBloque = horarioBloqueMapper.toEntity(horarioBloqueRequest);
        HorarioBloque created = horarioBloqueRepository.save(horarioBloque);
        return horarioBloqueMapper.toResponse(created);
    }

    @Transactional(readOnly = true)
    @Override
    public HorarioBloqueResponse obtenerHorarioBloquePorId(Long id) {
        HorarioBloque horarioBloque = horarioBloqueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro bloque horario con el id:" + id));
        return horarioBloqueMapper.toResponse(horarioBloque);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean estaDiponibleHorarioBloque(Long id) {
        HorarioBloqueResponse horarioBloqueResponse = obtenerHorarioBloquePorId(id);
        return horarioBloqueResponse.getEstadoBloque().equals("DISPONIBLE");
    }

    @Transactional
    @Override
    public HorarioBloqueResponse actualizarEstadoHorarioBloque(Long id, EstadoBloque estado) {
        HorarioBloque horarioBloque = horarioBloqueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro bloque horario con el id:" + id));
        horarioBloque.setEstadoBloque(estado);
        HorarioBloque updated = horarioBloqueRepository.save(horarioBloque);
        return horarioBloqueMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public void liberarHorarioBloque(Long id) {
        HorarioBloque horarioBloque = horarioBloqueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro bloque horario con el id:" + id));
        if (horarioBloque.getEstadoBloque() == EstadoBloque.DISPONIBLE) {
            throw new DuplicateResourceException("Ya esta liberado el bloque horario ingreado");
        }
        horarioBloque.setEstadoBloque(EstadoBloque.DISPONIBLE);
    }
}
