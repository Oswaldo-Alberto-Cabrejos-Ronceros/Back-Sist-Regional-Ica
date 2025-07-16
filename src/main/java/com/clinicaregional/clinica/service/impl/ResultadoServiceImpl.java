package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.PacienteConUserDTO;
import com.clinicaregional.clinica.dto.ResultadoArchivoDTO;
import com.clinicaregional.clinica.dto.request.ResultadoRequest;
import com.clinicaregional.clinica.dto.response.ResultadoResponse;
import com.clinicaregional.clinica.entity.Cita;
import com.clinicaregional.clinica.entity.HistorialClinico;
import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.repository.CitaRepository;
import com.clinicaregional.clinica.entity.Resultado;
import com.clinicaregional.clinica.enums.EstadoCita;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.ResultadoMapper;
import com.clinicaregional.clinica.repository.ResultadoRepository;
import com.clinicaregional.clinica.service.PacienteService;
import com.clinicaregional.clinica.service.CitaService;
import com.clinicaregional.clinica.mapper.PacienteMapper;
import com.clinicaregional.clinica.mapper.CitaMapper;
import com.clinicaregional.clinica.service.ResultadoService;
import com.clinicaregional.clinica.util.FiltroEstado;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultadoServiceImpl implements ResultadoService {

    private final ResultadoMapper resultadoMapper;
    private final ResultadoRepository resultadoRepository;
    private final FiltroEstado filtroEstado;
    private final PacienteService pacienteService;
    private final PacienteMapper pacienteMapper;
    private final CitaService citaService;
    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;
    private final HistorialClinicoServiceImpl historialClinicoServiceImpl;

    @Autowired
    public ResultadoServiceImpl(ResultadoMapper resultadoMapper, ResultadoRepository resultadoRepository,
            FiltroEstado filtroEstado, PacienteService pacienteService, PacienteMapper pacienteMapper,
            CitaService citaService, CitaRepository citaRepository, CitaMapper citaMapper,
            HistorialClinicoServiceImpl historialClinicoServiceImpl) {
        this.citaMapper = citaMapper;
        this.resultadoMapper = resultadoMapper;
        this.resultadoRepository = resultadoRepository;
        this.filtroEstado = filtroEstado;
        this.pacienteService = pacienteService;
        this.pacienteMapper = pacienteMapper;
        this.citaService = citaService;
        this.citaRepository = citaRepository;
        this.historialClinicoServiceImpl = historialClinicoServiceImpl;
    }

    @Transactional
    @Override
    public ResultadoResponse crear(ResultadoRequest resultadoRequest) {
        filtroEstado.activarFiltroEstado(true);

        Cita cita = citaRepository.findById(resultadoRequest.getCitaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        if (cita.getEstadoCita() != EstadoCita.CONFIRMADA) {
            throw new IllegalStateException("Solo se pueden registrar resultados para citas confirmadas.");
        }

        Paciente paciente = cita.getPaciente();
        HistorialClinico historialClinico = historialClinicoServiceImpl.obtenerOCrearHistorialPorPaciente(paciente);

        Resultado resultado = resultadoMapper.toEntity(resultadoRequest);
        resultado.setCita(cita);
        resultado.setHistorialClinico(historialClinico);
        resultado.setContieneArchivo(false);
        resultado.setArchivoKey(null);

        resultado = resultadoRepository.save(resultado);
        return resultadoMapper.toResponse(resultado);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResultadoResponse> obtenerPorCita(Long citaId) {
        filtroEstado.activarFiltroEstado(true);
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + citaId));
        return resultadoRepository.findAllByCita_Id(cita.getId())
                .stream()
                .map(resultadoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResultadoResponse> listarPorHistorialClinicoDePaciente(Long id) {
        filtroEstado.activarFiltroEstado(true);
        PacienteConUserDTO paciente = pacienteService.getPacientePorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + id));
        Paciente pacienteEntity = pacienteMapper.mapToPaciente(paciente);
        return resultadoRepository.findAllByHistorialClinico_Paciente_Id(pacienteEntity.getId())
                .stream()
                .map(resultadoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ResultadoResponse actualizar(Long id, ResultadoRequest resultadoRequest) {
        filtroEstado.activarFiltroEstado(true);
        Resultado resultado = resultadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado no encontrado con id: " + id));
        resultado.setDiagnostico(resultadoRequest.getDiagnostico());
        resultado.setTratamiento(resultadoRequest.getTratamiento());
        resultado.setNotasResultado(resultadoRequest.getNotasResultado());
        resultado = resultadoRepository.save(resultado);
        return resultadoMapper.toResponse(resultado);
    }

    @Transactional
    @Override
    public void eliminar(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Resultado resultado = resultadoRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado no encontrado con id: " + id));
        resultado.setEstado(false);
        resultadoRepository.save(resultado);
    }
}
