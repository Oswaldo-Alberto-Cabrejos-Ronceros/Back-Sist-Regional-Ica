package com.clinicaregional.clinica.service.impl;

import java.util.stream.Collectors;
import java.util.List;

import com.clinicaregional.clinica.dto.AdministradorDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponsePublicDTO;
import com.clinicaregional.clinica.dto.response.MyInfoMedico;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.service.TipoDocumentoService;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.exception.BadRequestException;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinicaregional.clinica.repository.MedicoRepository;
import com.clinicaregional.clinica.dto.request.MedicoRequestDTO;
import com.clinicaregional.clinica.dto.request.MedicoUpdateDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.UsuarioRequestDTO;
import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponseDTO;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.MedicoMapper;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.service.MedicoService;
import com.clinicaregional.clinica.service.RolService;
import com.clinicaregional.clinica.service.UsuarioService;

import org.springframework.transaction.annotation.Transactional;

@Service
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final TipoDocumentoService tipoDocumentoService;
    private final RolService rolService;
    private final FiltroEstado filtroEstado;

    @Autowired
    public MedicoServiceImpl(
            MedicoRepository medicoRepository,
            MedicoMapper medicoMapper,
            UsuarioRepository usuarioRepository,
            UsuarioService usuarioService,
            TipoDocumentoService tipoDocumentoService,
            RolService rolService,
            FiltroEstado filtroEstado) {

        this.medicoRepository = medicoRepository;
        this.medicoMapper = medicoMapper;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.tipoDocumentoService = tipoDocumentoService;
        this.rolService = rolService;
        this.filtroEstado = filtroEstado;
    }

    @Transactional(readOnly = true)
    @Override
    public List<MedicoResponseDTO> obtenerMedicos() {
        filtroEstado.activarFiltroEstado(true);
        return medicoRepository.findAll()
                .stream()
                .map(medicoMapper::mapToMedicoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<MedicoResponsePublicDTO> obtenerMedicosPublic() {
        filtroEstado.activarFiltroEstado(true);
        return medicoRepository.findAll().stream().map(medicoMapper::mapToMedicoResponsePublicDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public MedicoResponseDTO obtenerMedicoPorId(Long id) {
        filtroEstado.activarFiltroEstado(true);
        return medicoMapper.mapToMedicoResponseDTO(medicoRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medico no encontrado con ID: " + id)));
    }

    @Transactional(readOnly = true)
    @Override
    public MyInfoMedico obtenerMyInfoMedico(Long id) {
        Medico medico = medicoRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medico no encontrado con ID: " + id));
        return medicoMapper.mapToMyInfoMedico(medico);
    }

    @Transactional
    @Override
    public MedicoResponseDTO guardarMedico(MedicoRequestDTO dto) {
        filtroEstado.activarFiltroEstado(true);

        if (medicoRepository.existsByNumeroColegiatura(dto.getNumeroColegiatura())) {
            throw new DuplicateResourceException("Ya existe un médico con el número de colegiatura ingresado");
        }

        // Validar RNE solo si es ESPECIALISTA
        if (dto.getTipoMedico().name().equals("ESPECIALISTA")) {
            if (dto.getNumeroRNE() == null || dto.getNumeroRNE().isBlank()) {
                throw new BadRequestException("El número RNE es obligatorio para médicos especialistas");
            }
            if (medicoRepository.existsByNumeroRNE(dto.getNumeroRNE())) {
                throw new DuplicateResourceException("Ya existe un médico con el RNE ingresado");
            }
        } else {
            dto.setNumeroRNE(null); // limpiar por si se envió accidentalmente
        }

        // verificamos que exista tipo documento
        TipoDocumento tipoDocumento = tipoDocumentoService.getTipoDocumentoByIdContext(dto.getTipoDocumentoId())
                .orElseThrow(() -> new RuntimeException("No se encontró un tipo de documento con el id ingresado"));

        if (medicoRepository.existsByNumeroDocumento(dto.getNumeroDocumento())) {
            throw new RuntimeException("Ya existe un medico con el numero de documento ingresado");
        }

        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new DuplicateResourceException("Ya existe un usuario con el correo ingresado");
        }

        RolDTO rolMedico = rolService.obtenerRolPorNombre("MEDICO")
                .orElseThrow(() -> new IllegalStateException(
                        "Rol MEDICO no encontrado en el sistema"));
        // Crear usuario
        UsuarioRequestDTO newUsuario = new UsuarioRequestDTO();
        newUsuario.setCorreo(dto.getCorreo());
        newUsuario.setPassword(dto.getPassword());
        newUsuario.setRol(rolMedico);

        UsuarioDTO usuarioDTO = usuarioService.guardar(newUsuario);
        Usuario usuario1 = new Usuario();
        usuario1.setId(usuarioDTO.getId());

        Medico medico = Medico.builder()
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .numeroColegiatura(dto.getNumeroColegiatura())
                .numeroRNE(dto.getNumeroRNE())
                .tipoDocumento(tipoDocumento)
                .numeroDocumento(dto.getNumeroDocumento())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .descripcion(dto.getDescripcion())
                .imagen(dto.getImagen())
                .fechaContratacion(dto.getFechaContratacion())
                .tipoContrato(dto.getTipoContrato())
                .tipoMedico(dto.getTipoMedico())
                .usuario(usuario1)
                .estado(true)
                .build();

        return medicoMapper.mapToMedicoResponseDTO(medicoRepository.save(medico));
    }

    @Transactional
    @Override
    public MedicoResponseDTO actualizarMedico(Long id, MedicoUpdateDTO dto) {
        filtroEstado.activarFiltroEstado(true);

        Medico medico = medicoRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado con ID: " + id));

        Usuario usuario = medico.getUsuario();

        // Validar número de colegiatura si cambió
        if (dto.getNumeroColegiatura() != null &&
                !dto.getNumeroColegiatura().isBlank() &&
                !medico.getNumeroColegiatura().equals(dto.getNumeroColegiatura()) &&
                medicoRepository.existsByNumeroColegiatura(dto.getNumeroColegiatura())) {
            throw new DuplicateResourceException("Ya existe un médico con el número de colegiatura ingresado");
        }

        // Validar RNE si es especialista
        if (dto.getTipoMedico() != null && dto.getTipoMedico().name().equals("ESPECIALISTA")) {
            if (dto.getNumeroRNE() == null || dto.getNumeroRNE().isBlank()) {
                throw new BadRequestException("El número RNE es obligatorio para médicos especialistas");
            }
            if (!dto.getNumeroRNE().equals(medico.getNumeroRNE()) &&
                    medicoRepository.existsByNumeroRNE(dto.getNumeroRNE())) {
                throw new DuplicateResourceException("Ya existe un médico con el RNE ingresado");
            }
        } else {
            dto.setNumeroRNE(medico.getNumeroRNE()); // conserva valor anterior
        }

        // Validar número de documento si cambió
        if (dto.getNumeroDocumento() != null &&
                !dto.getNumeroDocumento().equals(medico.getNumeroDocumento()) &&
                medicoRepository.existsByNumeroDocumento(dto.getNumeroDocumento())) {
            throw new DuplicateResourceException("Ya existe un médico con el número de documento ingresado");
        }

        // Validar correo si cambió
        if (dto.getCorreo() != null &&
                !dto.getCorreo().equals(usuario.getCorreo()) &&
                usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new DuplicateResourceException("Ya existe un usuario con el correo ingresado");
        }

        Medico medicoConCorreo = medicoRepository.findByUsuarioCorreo(dto.getCorreo()).orElse(null);
        if (medicoConCorreo != null && !medicoConCorreo.getId().equals(medico.getId())) {
            throw new DuplicateResourceException("Ya existe un médico con el usuario ingresado");
        }

        // Actualizar correo del usuario si viene
        if (dto.getCorreo() != null && !dto.getCorreo().isBlank()) {
            usuario.setCorreo(dto.getCorreo());
            usuarioRepository.save(usuario);
        }

        // Actualizar tipo de documento si cambió
        if (dto.getTipoDocumentoId() != null &&
                !medico.getTipoDocumento().getId().equals(dto.getTipoDocumentoId())) {
            TipoDocumento tipoDocumento = tipoDocumentoService.getTipoDocumentoByIdContext(dto.getTipoDocumentoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No se encontró tipo de documento con ID: " + dto.getTipoDocumentoId()));
            medico.setTipoDocumento(tipoDocumento);
        }

        // Actualizar datos del médico solo si vienen
        if (dto.getNombres() != null && !dto.getNombres().isBlank()) {
            medico.setNombres(dto.getNombres());
        }

        if (dto.getApellidos() != null && !dto.getApellidos().isBlank()) {
            medico.setApellidos(dto.getApellidos());
        }

        if (dto.getNumeroColegiatura() != null && !dto.getNumeroColegiatura().isBlank()) {
            medico.setNumeroColegiatura(dto.getNumeroColegiatura());
        }

        if (dto.getNumeroRNE() != null && !dto.getNumeroRNE().isBlank()) {
            medico.setNumeroRNE(dto.getNumeroRNE());
        }

        if (dto.getNumeroDocumento() != null && !dto.getNumeroDocumento().isBlank()) {
            medico.setNumeroDocumento(dto.getNumeroDocumento());
        }

        if (dto.getTelefono() != null && !dto.getTelefono().isBlank()) {
            medico.setTelefono(dto.getTelefono());
        }

        if (dto.getDireccion() != null && !dto.getDireccion().isBlank()) {
            medico.setDireccion(dto.getDireccion());
        }

        if (dto.getDescripcion() != null && !dto.getDescripcion().isBlank()) {
            medico.setDescripcion(dto.getDescripcion());
        }

        if (dto.getImagen() != null && !dto.getImagen().isBlank()) {
            medico.setImagen(dto.getImagen());
        }

        if (dto.getTipoContrato() != null) {
            medico.setTipoContrato(dto.getTipoContrato());
        }

        if (dto.getTipoMedico() != null) {
            medico.setTipoMedico(dto.getTipoMedico());
        }

        Medico actualizado = medicoRepository.save(medico);
        return medicoMapper.mapToMedicoResponseDTO(actualizado);
    }

    @Transactional
    @Override
    public void eliminarMedico(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Medico medico = medicoRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado con ID: " + id));

        // 1. Primero marca el médico como inactivo
        medico.setEstado(false);
        medicoRepository.save(medico);

        // 2. Desvincula el usuario (si existe)
        if (medico.getUsuario() != null) {
            Long usuarioId = medico.getUsuario().getId();

            // 3. Elimina el usuario en una nueva transacción
            try {
                usuarioService.eliminarUsuarioSinRelaciones(usuarioId);

                // 4. Actualiza el médico para establecer usuario_id como null
                medico.setUsuario(null);
                medicoRepository.save(medico);
            } catch (Exception e) {
                // Loggear el error pero continuar
                System.err.println("Error al eliminar usuario asociado: " + e.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public MedicoResponseDTO obtenerMedicoPorUsuarioId(Long usuarioId) {
        filtroEstado.activarFiltroEstado(true);
        Medico medico = medicoRepository.findByUsuario_Id(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado con usuario ID: " + usuarioId));
        return medicoMapper.mapToMedicoResponseDTO(medico);
    }
}
