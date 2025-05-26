package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.AlergiaDTO;
import com.clinicaregional.clinica.dto.PacienteAlergiaDTO;
import com.clinicaregional.clinica.dto.PacienteDTO;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.entity.PacienteAlergia;
import com.clinicaregional.clinica.mapper.AlergiaMapper;
import com.clinicaregional.clinica.mapper.PacienteAlergiaMapper;
import com.clinicaregional.clinica.mapper.PacienteMapper;
import com.clinicaregional.clinica.repository.PacienteAlergiaRepository;
import com.clinicaregional.clinica.service.AlergiaService;
import com.clinicaregional.clinica.service.PacienteAlergiaService;
import com.clinicaregional.clinica.service.PacienteService;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PacienteAlergiaServiceImpl implements PacienteAlergiaService {

    private final PacienteAlergiaRepository pacienteAlergiaRepository;
    private final PacienteAlergiaMapper pacienteAlergiaMapper;
    private final PacienteService pacienteService;
    private final PacienteMapper pacienteMapper;
    private final AlergiaService alergiaService;
    private final AlergiaMapper alergiaMapper;
    private final FiltroEstado filtroEstado;

    @Autowired
    public PacienteAlergiaServiceImpl(
            PacienteAlergiaRepository pacienteAlergiaRepository,
            PacienteAlergiaMapper pacienteAlergiaMapper,
            PacienteService pacienteService,
            PacienteMapper pacienteMapper,
            AlergiaService alergiaService,
            AlergiaMapper alergiaMapper,
            FiltroEstado filtroEstado) {
        this.pacienteAlergiaRepository = pacienteAlergiaRepository;
        this.pacienteAlergiaMapper = pacienteAlergiaMapper;
        this.pacienteService = pacienteService;
        this.pacienteMapper = pacienteMapper;
        this.alergiaService = alergiaService;
        this.alergiaMapper = alergiaMapper;
        this.filtroEstado = filtroEstado;
    }

    @Override
    public List<PacienteAlergiaDTO> listarPacienteAlergias() {
        filtroEstado.activarFiltroEstado(true);
        return pacienteAlergiaRepository.findAll()
                .stream()
                .map(pacienteAlergiaMapper::mapToPacienteAlergiaDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PacienteAlergiaDTO> listarPacienteAlergiasPorPaciente(Long id) {
        filtroEstado.activarFiltroEstado(true);
        PacienteDTO findPaciente = pacienteService.getPacientePorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el paciente con id: " + id));
        Paciente paciente = pacienteMapper.mapToPaciente(findPaciente);
        return pacienteAlergiaRepository.findByPaciente(paciente)
                .stream()
                .map(pacienteAlergiaMapper::mapToPacienteAlergiaDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PacienteAlergiaDTO> getPacienteAlergiaById(Long id) {
        filtroEstado.activarFiltroEstado(true);
        return pacienteAlergiaRepository.findByIdAndEstadoIsTrue(id)
                .map(pacienteAlergiaMapper::mapToPacienteAlergiaDTO);
    }

    @Override
    public PacienteAlergiaDTO createPacienteAlergia(PacienteAlergiaDTO pacienteAlergiaDTO) {
        filtroEstado.activarFiltroEstado(true);
        PacienteDTO pacienteDTO = pacienteService.getPacientePorId(pacienteAlergiaDTO.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró paciente con el id ingresado"));

        AlergiaDTO alergiaDTO = alergiaService.getAlergiaPorId(pacienteAlergiaDTO.getAlergia().getId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró alergia con el id ingresado"));

        if (pacienteAlergiaRepository.existsByPacienteAndAlergia(
                pacienteMapper.mapToPaciente(pacienteDTO),
                alergiaMapper.mapToAlergia(alergiaDTO))) {
            throw new DuplicateResourceException("Paciente Alergia ya existe");
        }

        pacienteAlergiaDTO.setAlergia(alergiaDTO);
        PacienteAlergia pacienteAlergia = pacienteAlergiaMapper.mapToPacienteAlergia(pacienteAlergiaDTO);
        pacienteAlergia.setPaciente(pacienteMapper.mapToPaciente(pacienteDTO));
        PacienteAlergia savedPacienteAlergia = pacienteAlergiaRepository.save(pacienteAlergia);
        return pacienteAlergiaMapper.mapToPacienteAlergiaDTO(savedPacienteAlergia);
    }

    @Override
    public PacienteAlergiaDTO updatePacienteAlergia(Long id, PacienteAlergiaDTO pacienteAlergiaDTO) {
        filtroEstado.activarFiltroEstado(true);
        PacienteAlergia pacienteAlergia = pacienteAlergiaRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró alergia paciente con el id ingresado"));

        pacienteAlergia.setGravedad(pacienteAlergiaDTO.getGravedad());
        PacienteAlergia updatedPacienteAlergia = pacienteAlergiaRepository.save(pacienteAlergia);
        return pacienteAlergiaMapper.mapToPacienteAlergiaDTO(updatedPacienteAlergia);
    }

    @Override
    public void deletePacienteAlergia(Long id) {
        filtroEstado.activarFiltroEstado(true);
        PacienteAlergia pacienteAlergia = pacienteAlergiaRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró alergia paciente con el id ingresado"));
        pacienteAlergia.setEstado(false); // Borrado lógico
        pacienteAlergiaRepository.save(pacienteAlergia);
    }
}
