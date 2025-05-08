package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.PacienteDTO;
import com.clinicaregional.clinica.dto.SeguroDTO;
import com.clinicaregional.clinica.dto.TipoDocumentoDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.entity.Seguro;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.mapper.PacienteMapper;
import com.clinicaregional.clinica.repository.PacienteRepository;
import com.clinicaregional.clinica.service.PacienteService;
import com.clinicaregional.clinica.service.SeguroService;
import com.clinicaregional.clinica.service.TipoDocumentoService;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;
    private final TipoDocumentoService tipoDocumentoService;
    private final UsuarioService usuarioService;
    private final SeguroService seguroService;
    private final FiltroEstado filtroEstado;

    @Autowired
    public PacienteServiceImpl(
            PacienteRepository pacienteRepository,
            PacienteMapper pacienteMapper,
            TipoDocumentoService tipoDocumentoService,
            UsuarioService usuarioService,
            SeguroService seguroService,
            FiltroEstado filtroEstado) {
        this.pacienteRepository = pacienteRepository;
        this.pacienteMapper = pacienteMapper;
        this.tipoDocumentoService = tipoDocumentoService;
        this.usuarioService = usuarioService;
        this.seguroService = seguroService;
        this.filtroEstado = filtroEstado;
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

    @Transactional
    @Override
    public PacienteDTO crearPaciente(PacienteDTO pacienteDTO) {
        filtroEstado.activarFiltroEstado(true);
        if (pacienteRepository.findByNumeroIdentificacion(pacienteDTO.getNumeroIdentificacion()).isPresent()) {
            throw new RuntimeException("Ya existe un paciente con ese número de identificación");
        }

        TipoDocumento tipoDocumento = tipoDocumentoService.getTipoDocumentoByIdContext(pacienteDTO.getTipoDocumento().getId())
                .orElseThrow(() -> new RuntimeException("No se encontró un tipo de documento con el id ingresado"));

        Paciente paciente = pacienteMapper.mapToPaciente(pacienteDTO);
        paciente.setTipoDocumento(tipoDocumento);

        if (paciente.getUsuario() != null) {
            Usuario usuario = usuarioService.obtenerPorIdContenxt(pacienteDTO.getUsuario().getId())
                    .orElseThrow(() -> new RuntimeException("No se encontró un usuario con el id ingresado"));
            paciente.setUsuario(usuario);
        }

        if(paciente.getSeguro() != null) {
            seguroService.getSeguroById(paciente.getSeguro().getId()).orElseThrow(()->new RuntimeException("No se encontro seguro con el id ingresado"));
        }


        Paciente savedPaciente = pacienteRepository.save(paciente);
        return pacienteMapper.mapToPacienteDTO(savedPaciente);
    }

    @Transactional
    @Override
    public PacienteDTO actualizarPaciente(Long id, PacienteDTO pacienteDTO) {
        filtroEstado.activarFiltroEstado(true);
        Paciente paciente = pacienteRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        if (pacienteRepository.findByNumeroIdentificacion(pacienteDTO.getNumeroIdentificacion()).isPresent()) {
            throw new RuntimeException("Ya existe un paciente con ese número de identificación");
        }

        paciente.setNombres(pacienteDTO.getNombres());
        paciente.setApellidos(pacienteDTO.getApellidos());
        paciente.setFechaNacimiento(pacienteDTO.getFechaNacimiento());
        paciente.setSexo(pacienteDTO.getSexo());
        paciente.setTipoDocumento(paciente.getTipoDocumento());
        paciente.setNumeroIdentificacion(pacienteDTO.getNumeroIdentificacion());
        paciente.setTelefono(pacienteDTO.getTelefono());
        paciente.setDireccion(pacienteDTO.getDireccion());
        paciente.setTipoSangre(pacienteDTO.getTipoSangre());
        paciente.setAntecedentes(pacienteDTO.getAntecedentes());

        if(pacienteDTO.getSeguroId()!=null) {
            seguroService.getSeguroById(pacienteDTO.getSeguroId()).orElseThrow(()->new RuntimeException("No se encontro seguro con el id ingresado"));
            Seguro seguro = new Seguro();
            seguro.setId(pacienteDTO.getSeguroId());
            paciente.setSeguro(seguro);
        }

        Paciente updatedPaciente = pacienteRepository.save(paciente);
        return pacienteMapper.mapToPacienteDTO(updatedPaciente);
    }

    @Transactional
    @Override
    public void eliminarPaciente(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Paciente paciente = pacienteRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        paciente.setEstado(false); //borrado logico
        usuarioService.eliminar(paciente.getUsuario().getId());
        paciente.setUsuario(null);
        pacienteRepository.save(paciente);
    }
}
