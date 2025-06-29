package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.request.ResultadoRequest;
import com.clinicaregional.clinica.dto.response.ResultadoResponse;
import com.clinicaregional.clinica.dto.PacienteDTO;
import com.clinicaregional.clinica.entity.Cita;
import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.repository.CitaRepository;
import com.clinicaregional.clinica.entity.Resultado;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.ResultadoMapper;
import com.clinicaregional.clinica.repository.ResultadoRepository;
import com.clinicaregional.clinica.service.PacienteService;
import com.clinicaregional.clinica.service.CitaService;
import com.clinicaregional.clinica.mapper.PacienteMapper;
import com.clinicaregional.clinica.mapper.CitaMapper;
import com.clinicaregional.clinica.service.ResultadoService;
import com.clinicaregional.clinica.service.S3Service;
import com.clinicaregional.clinica.util.FiltroEstado;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final S3Service s3Service;

    @Autowired
    public ResultadoServiceImpl(ResultadoMapper resultadoMapper, ResultadoRepository resultadoRepository,
                                FiltroEstado filtroEstado, PacienteService pacienteService, PacienteMapper pacienteMapper,
                                CitaService citaService, CitaRepository citaRepository, CitaMapper citaMapper, S3Service s3Service) {
        this.citaMapper = citaMapper;
        this.resultadoMapper = resultadoMapper;
        this.resultadoRepository = resultadoRepository;
        this.filtroEstado = filtroEstado;
        this.pacienteService = pacienteService;
        this.pacienteMapper = pacienteMapper;
        this.citaService = citaService;
        this.citaRepository = citaRepository;
        this.s3Service = s3Service;
    }

    @Transactional
    @Override
    public ResultadoResponse crear(ResultadoRequest resultadoRequest, MultipartFile archivo) {
        filtroEstado.activarFiltroEstado(true);
        Resultado resultado = resultadoMapper.toEntity(resultadoRequest);
        if (archivo != null) {
            String key = s3Service.subirArchivo(archivo, resultado.getHistorialClinico().getId().toString());
            resultado.setContieneArchivo(true);
            resultado.setArchivoKey(key);
        }
        resultado = resultadoRepository.save(resultado);
        return resultadoMapper.toResponse(resultado);
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] recuperarArchivoByResultadoId(Long resultadoId) {
        filtroEstado.activarFiltroEstado(true);
        //obtenemos resultado
        Resultado resultado = resultadoRepository.findByIdAndEstadoIsTrue(resultadoId).orElseThrow(() -> new ResourceNotFoundException("Resultado no encontrado"));
        //obtenemos si tiene archivo
        Boolean contieneArchivo = resultado.getContieneArchivo();
        String archivoKey = resultado.getArchivoKey();
        if (!contieneArchivo | !archivoKey.isEmpty()) {
            throw new ResourceNotFoundException("El resultado no contiene archivo");
        }
        return s3Service.recuperarArchivo(archivoKey);
    }

    @Transactional
    @Override
    public ResultadoResponse agregarArchivoResultado(Long resultadoId, MultipartFile archivo) {
        filtroEstado.activarFiltroEstado(true);
        //obtenemos resultado
        Resultado resultado = resultadoRepository.findByIdAndEstadoIsTrue(resultadoId).orElseThrow(() -> new ResourceNotFoundException("Resultado no encontrado"));
        //obtenemos si tiene archivo
        Boolean contieneArchivo = resultado.getContieneArchivo();
        String archivoKey = resultado.getArchivoKey();
        if(contieneArchivo | !archivoKey.isEmpty()) {
            throw new ResourceNotFoundException("El resultado ya contiene archivos");
        }
        String newArchivoKey = s3Service.subirArchivo(archivo, resultado.getHistorialClinico().getId().toString());
        resultado.setContieneArchivo(true);
        resultado.setArchivoKey(newArchivoKey);
        Resultado updated = resultadoRepository.save(resultado);
        return resultadoMapper.toResponse(updated);
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
    public List<ResultadoResponse> listarPorHistorialClinicoDePaciente(Long Id) {
        filtroEstado.activarFiltroEstado(true);
        PacienteDTO paciente = pacienteService.getPacientePorId(Id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + Id));
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
