package com.clinicaregional.clinica.service.impl;


import com.clinicaregional.clinica.dto.PacienteDTO;
import com.clinicaregional.clinica.dto.response.MyInfoPaciente;
import com.clinicaregional.clinica.dto.response.PagedResponse;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.PacienteMapper;
import com.clinicaregional.clinica.repository.PacienteRepository;
import com.clinicaregional.clinica.service.PacienteService;
import com.clinicaregional.clinica.service.S3ServicePublic;
import com.clinicaregional.clinica.service.TipoDocumentoService;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;
    private final TipoDocumentoService tipoDocumentoService;
    private final UsuarioService usuarioService;
    private final FiltroEstado filtroEstado;
    private final S3ServicePublic s3Service;

    @Autowired
    public PacienteServiceImpl(
            PacienteRepository pacienteRepository,
            PacienteMapper pacienteMapper,
            TipoDocumentoService tipoDocumentoService,
            UsuarioService usuarioService,
            FiltroEstado filtroEstado, S3ServicePublic s3Service) {
        this.pacienteRepository = pacienteRepository;
        this.pacienteMapper = pacienteMapper;
        this.tipoDocumentoService = tipoDocumentoService;
        this.usuarioService = usuarioService;
        this.filtroEstado = filtroEstado;
        this.s3Service = s3Service;
    }

    @Transactional(readOnly = true)
    @Override
    public List<PacienteDTO> listarPacientes() {
        filtroEstado.activarFiltroEstado(true);
        List<PacienteDTO> pacientes = pacienteRepository.findAll()
                .stream()
                .map(pacienteMapper::mapToPacienteDTO)
                .toList();
        return pacientes.stream().map(this::agregarUrlImage).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<PacienteDTO> getPacientePorId(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Optional<PacienteDTO> paciente = pacienteRepository.findByIdAndEstadoIsTrue(id)
                .map(pacienteMapper::mapToPacienteDTO);
        return paciente.map(this::agregarUrlImage);
                
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<PacienteDTO> getPacientePorIdentificacion(String identificacion) {
        filtroEstado.activarFiltroEstado(true);
        Optional<PacienteDTO> paciente = pacienteRepository.findByNumeroIdentificacion(identificacion)
                .map(pacienteMapper::mapToPacienteDTO);
        return paciente.map(this::agregarUrlImage);
    }

    @Transactional(readOnly = true)
    @Override
    //despues agregar validacion de owner
    public MyInfoPaciente getMyInfoPaciente(Long pacienteId) {
        Paciente paciente = pacienteRepository.findByIdAndEstadoIsTrue(pacienteId).orElseThrow(()->new ResourceNotFoundException("Paciente no encontrado con id" + pacienteId));
        if (paciente.getImagenUrl() != null) {
            String imageUrl = s3Service.generarUrlPublico(paciente.getImagenUrl());
            paciente.setImagenUrl(imageUrl);
        }
        return pacienteMapper.mapToMyInfoPaciente(paciente);
    }

    @Transactional
    @Override
    public PacienteDTO crearPaciente(PacienteDTO pacienteDTO, MultipartFile imagen) {
        filtroEstado.activarFiltroEstado(true);
        if (pacienteRepository.findByNumeroIdentificacion(pacienteDTO.getNumeroIdentificacion()).isPresent()) {
            throw new DuplicateResourceException("Ya existe un paciente con ese número de identificación");
        }

        TipoDocumento tipoDocumento = tipoDocumentoService.getTipoDocumentoByIdContext(pacienteDTO.getTipoDocumento().getId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un tipo de documento con el id ingresado"));

        Paciente paciente = pacienteMapper.mapToPaciente(pacienteDTO);
        paciente.setTipoDocumento(tipoDocumento);

        if (paciente.getUsuario() != null) {
            Usuario usuario = usuarioService.obtenerPorIdContenxt(pacienteDTO.getUsuario().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró un usuario con el id ingresado"));
            paciente.setUsuario(usuario);
        }

        if (imagen != null) {
            String key = s3Service.subirArchivo(imagen, "paciente" + paciente.getNombres());
            paciente.setImagenUrl(key);
        }

        Paciente savedPaciente = pacienteRepository.save(paciente);
        return this.agregarUrlImage(pacienteMapper.mapToPacienteDTO(savedPaciente));
    }

    @Transactional
    @Override
    public PacienteDTO actualizarPaciente(Long id, PacienteDTO pacienteDTO, MultipartFile imagen) {
        filtroEstado.activarFiltroEstado(true);
        Paciente paciente = pacienteRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        if (pacienteRepository.findByNumeroIdentificacion(pacienteDTO.getNumeroIdentificacion()).isPresent()) {
            throw new DuplicateResourceException("Ya existe un paciente con ese número de identificación");
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

        if (imagen != null) {
            if (paciente.getImagenUrl() != null) {
                s3Service.eliminarArchivo(paciente.getImagenUrl());
            }
            String key = s3Service.subirArchivo(imagen, "paciente" + paciente.getNombres());
            paciente.setImagenUrl(key);
        }

        Paciente updatedPaciente = pacienteRepository.save(paciente);
        return this.agregarUrlImage(pacienteMapper.mapToPacienteDTO(updatedPaciente));
    }

    @Transactional
    @Override
    public void eliminarPaciente(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Paciente paciente = pacienteRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));
        paciente.setEstado(false); //borrado logico
        usuarioService.eliminar(paciente.getUsuario().getId());
        paciente.setUsuario(null);
        if (paciente.getImagenUrl() != null) {
            s3Service.eliminarArchivo(paciente.getImagenUrl());
        }
        pacienteRepository.save(paciente);
    }

    @Override
    public PagedResponse<PacienteDTO> listarPacientesPaginado(Pageable pageable) {
        filtroEstado.activarFiltroEstado(true);
        Page<Paciente> paginaPacientes = pacienteRepository.findAllByEstadoIsTrue(pageable);

        List<PacienteDTO> contenido = paginaPacientes.getContent()
                .stream()
                .map(pacienteMapper::mapToPacienteDTO)
                .toList();
        List<PacienteDTO> contenidoWithUrlImage = contenido.stream().map(this::agregarUrlImage).toList();
        return new PagedResponse<>(
                contenidoWithUrlImage,
                paginaPacientes.getNumber(),
                paginaPacientes.getSize(),
                paginaPacientes.getTotalElements(),
                paginaPacientes.getTotalPages(),
                paginaPacientes.isLast()
        );
    }

    //funcion para obtener el url
    private PacienteDTO agregarUrlImage(PacienteDTO pacienteDTO) {
        if (pacienteDTO.getImagenUrl() != null) {
            String imageUrl = s3Service.generarUrlPublico(pacienteDTO.getImagenUrl());
            pacienteDTO.setImagenUrl(imageUrl);
        }
        return pacienteDTO;
    }

}
