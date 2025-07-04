package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.PacienteDTO;
import com.clinicaregional.clinica.dto.PacienteSimpleDTO;
import com.clinicaregional.clinica.dto.TipoDocumentoDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.UpdatePacienteDTO;
import com.clinicaregional.clinica.dto.response.MyInfoPaciente;
import com.clinicaregional.clinica.dto.response.PagedResponse;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.entity.Seguro;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.PacienteMapper;
import com.clinicaregional.clinica.repository.PacienteRepository;
import com.clinicaregional.clinica.service.PacienteService;
import com.clinicaregional.clinica.service.TipoDocumentoService;
import com.clinicaregional.clinica.mapper.SeguroMapper;
import com.clinicaregional.clinica.repository.SeguroRepository;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.DuplicateFormatFlagsException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@Service
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;
    private final TipoDocumentoService tipoDocumentoService;
    private final UsuarioService usuarioService;
    private final FiltroEstado filtroEstado;
    private final SeguroRepository seguroRepository;
    private final SeguroMapper seguroMapper;

    @Autowired
    public PacienteServiceImpl(
            PacienteRepository pacienteRepository,
            PacienteMapper pacienteMapper,
            TipoDocumentoService tipoDocumentoService,
            UsuarioService usuarioService,
            FiltroEstado filtroEstado
            , SeguroRepository seguroRepository, SeguroMapper seguroMapper) {
        this.pacienteRepository = pacienteRepository;
        this.pacienteMapper = pacienteMapper;
        this.tipoDocumentoService = tipoDocumentoService;
        this.usuarioService = usuarioService;
        this.filtroEstado = filtroEstado;
        this.seguroRepository = seguroRepository;
        this.seguroMapper = seguroMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public List<PacienteDTO> listarPacientes() {
        filtroEstado.activarFiltroEstado(true);
        return pacienteRepository.findAll()
                .stream()
                .map(pacienteMapper::mapToPacienteDTO)
                .collect(Collectors.toList());
    }

    // Listar pacientes sin importar el filtro de estado
    @Transactional(readOnly = true)
    @Override
    public List<PacienteDTO> listarPacientesPorEstado() {
        return pacienteRepository.findAll()
                .stream()
                .map(pacienteMapper::mapToPacienteDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<PacienteDTO> getPacientePorId(Long id) {
        filtroEstado.activarFiltroEstado(true);
        return pacienteRepository.findByIdAndEstadoIsTrue(id)
                .map(pacienteMapper::mapToPacienteDTO);

    }

    @Transactional(readOnly = true)
    @Override
    public Optional<PacienteDTO> getPacientePorIdentificacion(String identificacion) {
        filtroEstado.activarFiltroEstado(true);
        return pacienteRepository.findByNumeroIdentificacion(identificacion)
                .map(pacienteMapper::mapToPacienteDTO);
    }

    @Transactional(readOnly = true)
    @Override
    // despues agregar validacion de owner
    public MyInfoPaciente getMyInfoPaciente(Long pacienteId) {
        Paciente paciente = pacienteRepository.findByIdAndEstadoIsTrue(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id" + pacienteId));
        return pacienteMapper.mapToMyInfoPaciente(paciente);
    }

    // Crear paciente sin usuario
    @Transactional
    @Override
    public PacienteSimpleDTO crearPacienteSimple(PacienteSimpleDTO pacienteSimpleDTO) {
        filtroEstado.activarFiltroEstado(true);

        if (pacienteRepository.findByNumeroIdentificacion(pacienteSimpleDTO.getNumeroIdentificacion()).isPresent()) {
            throw new DuplicateResourceException("Ya existe un paciente con ese número de identificación");
        }

        TipoDocumento tipoDocumento = tipoDocumentoService
                .getTipoDocumentoByIdContext(pacienteSimpleDTO.getTipoDocumento().getId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("No se encontró un tipo de documento con el id ingresado"));

        Seguro seguro;
        if (pacienteSimpleDTO.getSeguro() != null && pacienteSimpleDTO.getSeguro().getId() != null) {
            seguro = seguroRepository.findById(pacienteSimpleDTO.getSeguro().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró un seguro con el id ingresado"));
        } else {
            seguro = seguroRepository.save(seguroMapper.mapToSeguro(pacienteSimpleDTO.getSeguro()));
        }

        Paciente paciente = pacienteMapper.mapToPacienteSimple(pacienteSimpleDTO);
        paciente.setTipoDocumento(tipoDocumento);
        paciente.setSeguro(seguro);

        Paciente savedPaciente = pacienteRepository.save(paciente);

        return pacienteMapper.mapToPacienteSimpleDTO(savedPaciente);
    }

    @Transactional
    @Override
    public PacienteDTO crearPaciente(PacienteDTO pacienteDTO) {
        filtroEstado.activarFiltroEstado(true);
        if (pacienteRepository.findByNumeroIdentificacion(pacienteDTO.getNumeroIdentificacion()).isPresent()) {
            throw new DuplicateResourceException("Ya existe un paciente con ese número de identificación");
        }

        TipoDocumento tipoDocumento = tipoDocumentoService
                .getTipoDocumentoByIdContext(pacienteDTO.getTipoDocumento().getId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("No se encontró un tipo de documento con el id ingresado"));

        Paciente paciente = pacienteMapper.mapToPaciente(pacienteDTO);
        paciente.setTipoDocumento(tipoDocumento);

        if (paciente.getUsuario() != null) {
            Usuario usuario = usuarioService.obtenerPorIdContenxt(pacienteDTO.getUsuario().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró un usuario con el id ingresado"));
            paciente.setUsuario(usuario);
        }

        Paciente savedPaciente = pacienteRepository.save(paciente);
        return pacienteMapper.mapToPacienteDTO(savedPaciente);
    }

    @Transactional
    @Override
    public PacienteDTO actualizarPaciente(Long id, UpdatePacienteDTO updatePacienteDTO) {
        Paciente paciente = pacienteRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + id));

        // Actualizar solo los campos proporcionados
        if (updatePacienteDTO.getTelefono() != null) {
            paciente.setTelefono(updatePacienteDTO.getTelefono());
        }

        if (updatePacienteDTO.getDireccion() != null) {
            paciente.setDireccion(updatePacienteDTO.getDireccion());
        }

        if (updatePacienteDTO.getImagenUrl() != null) {
            paciente.setImagenUrl(updatePacienteDTO.getImagenUrl());
        }

        if (updatePacienteDTO.getAntecedentes() != null) {
            paciente.setAntecedentes(updatePacienteDTO.getAntecedentes());
        }

        Paciente updatedPaciente = pacienteRepository.save(paciente);
        return pacienteMapper.mapToPacienteDTO(updatedPaciente);
    }

    @Transactional
    @Override
    public void eliminarPaciente(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Paciente paciente = pacienteRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        // 1. Primero desvincula el usuario (si existe)
        if (paciente.getUsuario() != null) {
            Long usuarioId = paciente.getUsuario().getId();
            paciente.setUsuario(null);
            pacienteRepository.save(paciente);

            // 2. Luego marca el paciente como inactivo
            paciente.setEstado(false);
            pacienteRepository.save(paciente);

            // 3. Finalmente intenta eliminar el usuario en una nueva transacción
            try {
                usuarioService.eliminarUsuarioSinRelaciones(usuarioId);
            } catch (Exception e) {
                // Loggear el error pero continuar
                System.err.println("Error al eliminar usuario asociado: " + e.getMessage());
            }
        } else {
            // Si no tiene usuario, solo marcar como inactivo
            paciente.setEstado(false);
            pacienteRepository.save(paciente);
        }
    }

    @Override
    public PagedResponse<PacienteDTO> listarPacientesPaginado(Pageable pageable) {
        filtroEstado.activarFiltroEstado(true);
        Page<Paciente> paginaPacientes = pacienteRepository.findAllByEstadoIsTrue(pageable);

        List<PacienteDTO> contenido = paginaPacientes.getContent()
                .stream()
                .map(pacienteMapper::mapToPacienteDTO)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                contenido,
                paginaPacientes.getNumber(),
                paginaPacientes.getSize(),
                paginaPacientes.getTotalElements(),
                paginaPacientes.getTotalPages(),
                paginaPacientes.isLast());
    }

}
