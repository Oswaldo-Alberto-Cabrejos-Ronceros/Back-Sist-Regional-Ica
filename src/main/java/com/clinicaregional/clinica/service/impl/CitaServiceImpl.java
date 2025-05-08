package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.request.CitaReprogramarRequest;
import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
import com.clinicaregional.clinica.entity.Cita;
import com.clinicaregional.clinica.enums.EstadoCita;
import com.clinicaregional.clinica.mapper.CitaMapper;
import com.clinicaregional.clinica.repository.CitaRepository;
import com.clinicaregional.clinica.service.CitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;

    @Transactional(readOnly = true)
    @Override
    public List<CitaResponse> listar() {
        List<Cita> citas = citaRepository.findAll();
        return citas.stream().map(citaMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<CitaResponse> obtenerPorId(Long id) {
        return citaRepository.findById(id).map(citaMapper::toResponse);
    }

    @Transactional
    @Override
    public CitaResponse guardar(CitaRequest citaRequest) {
        //bloque horario en estado CANCELADA o NO_ASISTIO disponibles
        if (citaRepository.existsByFechaAndHoraAndEstadoCitaNotAndEstadoCitaNot(citaRequest.getFecha(), citaRequest.getHora(), EstadoCita.CANCELADA, EstadoCita.EN_CURSO)) {
            throw new RuntimeException("Fecha y Hora no disponibles");
        }
        Cita savedCita = citaRepository.save(citaMapper.toEntity(citaRequest));
        return citaMapper.toResponse(savedCita);
    }

    @Transactional
    @Override
    public CitaResponse actualizar(Long id, CitaRequest citaRequest) {
        Cita existingCita = citaRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro la cita"));
        if (citaRepository.existsByFechaAndHoraAndEstadoCitaNotAndEstadoCitaNot(citaRequest.getFecha(), citaRequest.getHora(), EstadoCita.CANCELADA, EstadoCita.EN_CURSO)) {
            throw new RuntimeException("Fecha y Hora no disponibles");
        }
        existingCita.setFecha(citaRequest.getFecha());
        existingCita.setHora(citaRequest.getHora());
        existingCita.setEstadoCita(citaRequest.getEstadoCita());
        existingCita.setNotas(citaRequest.getNotas());
        existingCita.setAntecedentes(citaRequest.getAntecedentes());
        Cita updatedCita = citaRepository.save(existingCita);
        return citaMapper.toResponse(updatedCita);

    }

    @Transactional
    @Override
    public void cancelar(Long id) {
        Cita cita = citaRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro la cita"));
        if (cita.getEstadoCita() == EstadoCita.CANCELADA) {
            throw new RuntimeException("La cita ya esta cancelada");
        }
        if (cita.getEstadoCita() == EstadoCita.ATENDIDA) {
            throw new RuntimeException("La cita ya ha sido atendida");
        }
        cita.setEstadoCita(EstadoCita.CANCELADA);
        citaRepository.save(cita);
    }

    @Transactional
    @Override
    public CitaResponse reprogramar(Long id, CitaReprogramarRequest citaReprogramarRequest) {
        Cita cita = citaRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro la cita"));
        if (citaRepository.existsByFechaAndHoraAndEstadoCitaNotAndEstadoCitaNot(citaReprogramarRequest.getFecha(), citaReprogramarRequest.getHora(), EstadoCita.CANCELADA, EstadoCita.EN_CURSO)) {
            throw new RuntimeException("Fecha y Hora no disponibles");
        }
        if (cita.getEstadoCita() == EstadoCita.CANCELADA) {
            throw new RuntimeException("La cita ya esta cancelada");
        }
        if (cita.getEstadoCita() == EstadoCita.ATENDIDA) {
            throw new RuntimeException("La cita ya ha sido atendida");
        }
        cita.setEstadoCita(EstadoCita.REPROGRAMADA);
        cita.setFecha(citaReprogramarRequest.getFecha());
        cita.setHora(citaReprogramarRequest.getHora());
        citaRepository.save(cita);
        return citaMapper.toResponse(cita);
    }

    @Transactional
    @Override
    public void marcarAtentida(Long id) {
        Cita cita = citaRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro la cita"));
        if (cita.getEstadoCita() == EstadoCita.EN_CURSO) {
            cita.setEstadoCita(EstadoCita.ATENDIDA);
            citaRepository.save(cita);
        } else {
            throw new RuntimeException("La cita no esta en curso");
        }
    }

    @Transactional
    @Override
    public void marcoNoAsistio(Long id) {
        Cita cita = citaRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro la cita"));
        if (cita.getEstadoCita() == EstadoCita.CANCELADA) {
            throw new RuntimeException("La cita ya esta cancelada");
        }
        if (cita.getEstadoCita() == EstadoCita.ATENDIDA) {
            throw new RuntimeException("La cita ya ha sido atendida");
        }
        if (cita.getEstadoCita() == EstadoCita.NO_ASISTIO) {
            throw new RuntimeException("La cita ya esta marcada como No Asistio");
        }
        cita.setEstadoCita(EstadoCita.NO_ASISTIO);
        citaRepository.save(cita);
    }

}
