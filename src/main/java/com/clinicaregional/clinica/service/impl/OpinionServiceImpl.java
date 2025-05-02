package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.request.OpinionRequest;
import com.clinicaregional.clinica.dto.response.OpinionResponse;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Opinion;
import com.clinicaregional.clinica.entity.Paciente;
// import com.clinicaregional.clinica.entity.Cita; // Descomentar cuando Cita esté lista
import com.clinicaregional.clinica.mapper.OpinionMapper;
import com.clinicaregional.clinica.repository.MedicoRepository;
import com.clinicaregional.clinica.repository.OpinionRepository;
import com.clinicaregional.clinica.repository.PacienteRepository;
// import com.clinicaregional.clinica.repository.CitaRepository;
import com.clinicaregional.clinica.service.OpinionService;
import com.clinicaregional.clinica.util.FiltroEstado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpinionServiceImpl extends FiltroEstado implements OpinionService {

    private final OpinionRepository opinionRepository;
    private final OpinionMapper opinionMapper;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    //private final CitaRepository citaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OpinionResponse> listar() {
        activarFiltroEstado(true);
        return opinionRepository.findAllByVisibleIsTrue()
                .stream()
                .map(opinionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OpinionResponse> obtenerPorId(Long id) {
        activarFiltroEstado(true);
        return opinionRepository.findByIdAndVisibleIsTrue(id)
                .map(opinionMapper::toResponse);
    }

    @Override
    @Transactional
    public OpinionResponse guardar(OpinionRequest request) {
        activarFiltroEstado(true);

        Paciente paciente = pacienteRepository.findByIdAndEstadoIsTrue(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + request.getPacienteId()));

        Medico medico = medicoRepository.findByIdAndEstadoIsTrue(request.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con ID: " + request.getMedicoId()));

        // Cita cita = citaRepository.findByIdAndEstadoIsTrue(request.getCitaId())
        //         .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + request.getCitaId()));

        Opinion opinion = opinionMapper.toEntity(request, paciente, medico /*, cita */);
        Opinion saved = opinionRepository.save(opinion);

        return opinionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        activarFiltroEstado(true);
        Opinion opinion = opinionRepository.findByIdAndVisibleIsTrue(id)
                .orElseThrow(() -> new RuntimeException("Opinión no encontrada con ID: " + id));
        opinion.setVisible(false);
        opinionRepository.save(opinion);
    }
}
