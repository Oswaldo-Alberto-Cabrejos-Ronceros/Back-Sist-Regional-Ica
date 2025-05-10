package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.request.HistorialClinicoRequest;
import com.clinicaregional.clinica.dto.response.HistorialClinicoResponse;
import com.clinicaregional.clinica.entity.HistorialClinico;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.mapper.HistorialClinicoMapper;
import com.clinicaregional.clinica.repository.HistorialClinicoRepository;
import com.clinicaregional.clinica.service.HistorialClinicoService;
import com.clinicaregional.clinica.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class HistorialClinicoServiceImpl implements HistorialClinicoService {

    private final HistorialClinicoRepository historialClinicoRepository;
    private final HistorialClinicoMapper historialClinicoMapper;
    private final PacienteService pacienteService;

    @Autowired
    public HistorialClinicoServiceImpl(HistorialClinicoRepository historialClinicoRepository, PacienteService pacienteService,
                                       HistorialClinicoMapper historialClinicoMapper) {
        this.historialClinicoRepository = historialClinicoRepository;
        this.pacienteService = pacienteService;
        this.historialClinicoMapper = historialClinicoMapper;
    }

    @Transactional
    @Override
    public Optional<HistorialClinicoResponse> getHistorialClinicoByPaciente(Long pacienteId) {
        pacienteService.getPacientePorId(pacienteId).orElseThrow(
                () -> new RuntimeException("Paciente no encontrado")
        );
        return historialClinicoRepository.findByPaciente_Id(pacienteId).map(historialClinicoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public HistorialClinicoResponse createHistorislClinico(HistorialClinicoRequest historialClinicoRequest) {
        pacienteService.getPacientePorId(historialClinicoRequest.getPacienteId()).orElseThrow(
                () -> new RuntimeException("Paciente no encontrado")
        );
        Paciente paciente = Paciente.builder().id(historialClinicoRequest.getPacienteId()).build();
        HistorialClinico historialClinico = historialClinicoRepository.save(historialClinicoMapper.toEntity(historialClinicoRequest,paciente));
        return historialClinicoMapper.toResponse(historialClinico);
    }
}
