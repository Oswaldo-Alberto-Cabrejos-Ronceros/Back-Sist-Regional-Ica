package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.request.HistorialClinicoRequest;
import com.clinicaregional.clinica.dto.response.HistorialClinicoResponse;
import com.clinicaregional.clinica.entity.HistorialClinico;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.mapper.HistorialClinicoMapper;
import com.clinicaregional.clinica.repository.HistorialClinicoRepository;
import com.clinicaregional.clinica.service.HistorialClinicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HistorialClinicoServiceImpl implements HistorialClinicoService {

    private final HistorialClinicoRepository historialClinicoRepository;
    private final HistorialClinicoMapper historialClinicoMapper;

    @Override
    @Transactional
    public HistorialClinicoResponse crear(HistorialClinicoRequest historialClinicoRequest) {
        HistorialClinico historialClinico = historialClinicoMapper.toEntity(historialClinicoRequest);

        historialClinico.setFecha(LocalDate.now());
        historialClinico.setEstado(true);

        historialClinico = historialClinicoRepository.save(historialClinico);
        return historialClinicoMapper.toResponse(historialClinico);
    }

    @Override
    public HistorialClinicoResponse obtenerPorPacienteId(Long pacienteId) {
        HistorialClinico historialClinico = historialClinicoRepository.findByPaciente_Id(pacienteId)
                .orElseThrow(() -> new RuntimeException(
                        "Historial clínico no encontrado para el paciente con ID: " + pacienteId));
        return historialClinicoMapper.toResponse(historialClinico);
    }

    @Transactional
    public HistorialClinico obtenerOCrearHistorialPorPaciente(Paciente paciente) {
        return historialClinicoRepository.findByPaciente_Id(paciente.getId())
                .orElseGet(() -> {
                    HistorialClinico nuevoHistorial = HistorialClinico.builder()
                            .paciente(paciente)
                            .fecha(LocalDate.now())
                            .build();
                    return historialClinicoRepository.save(nuevoHistorial);
                });
    }

}
