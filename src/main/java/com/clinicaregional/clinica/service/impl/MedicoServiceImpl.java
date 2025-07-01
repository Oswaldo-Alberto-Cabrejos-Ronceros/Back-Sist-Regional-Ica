package com.clinicaregional.clinica.service.impl;

import java.util.stream.Collectors;
import java.util.List;

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
        return medicoRepository.findAll().stream().map(medicoMapper::mapToMedicoResponsePublicDTO).collect(Collectors.toList());
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

        //verificamos que exista tipo documento
        TipoDocumento tipoDocumento = tipoDocumentoService.getTipoDocumentoByIdContext(dto.getTipoDocumentoId())
                .orElseThrow(() -> new RuntimeException("No se encontró un tipo de documento con el id ingresado"));

        if(medicoRepository.existsByNumeroDocumento(dto.getNumeroDocumento())) {
            throw new RuntimeException("Ya existe un medico con el numero de documento ingresado");
        }

        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new DuplicateResourceException("Ya existe un usuario con el correo ingresado");
        }

        RolDTO rolMedico = rolService.obtenerRolPorNombre("MEDICO");

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
    public MedicoResponseDTO actualizarMedico(Long id, MedicoRequestDTO dto) {
        filtroEstado.activarFiltroEstado(true);

        Medico medico = medicoRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado con ID: " + id));

        Usuario usuario = medico.getUsuario(); // obtener usuario asociado al médico

        // Validar colegiatura si cambió
        if (!medico.getNumeroColegiatura().equals(dto.getNumeroColegiatura()) &&
                medicoRepository.existsByNumeroColegiatura(dto.getNumeroColegiatura())) {
            throw new DuplicateResourceException("Ya existe un médico con el número de colegiatura ingresado");
        }

        // Validar RNE si corresponde y cambió
        if (dto.getTipoMedico().name().equals("ESPECIALISTA")) {
            if (dto.getNumeroRNE() == null || dto.getNumeroRNE().isBlank()) {
                throw new BadRequestException("El número RNE es obligatorio para médicos especialistas");
            }
            if (!dto.getNumeroRNE().equals(medico.getNumeroRNE()) &&
                    medicoRepository.existsByNumeroRNE(dto.getNumeroRNE())) {
                throw new DuplicateResourceException("Ya existe un médico con el RNE ingresado");
            }
        } else {
            dto.setNumeroRNE(null); // limpiar si se envió por error
        }

        // Validar si el correo fue modificado y ya existe en otro usuario
        if (!usuario.getCorreo().equals(dto.getCorreo()) &&
                usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new DuplicateResourceException("Ya existe un usuario con el correo ingresado");
        }

        // Validar si ese correo ya está asignado a otro médico
        Medico medicoConCorreo = medicoRepository.findByUsuarioCorreo(dto.getCorreo()).orElse(null);
        if (medicoConCorreo != null && !medicoConCorreo.getId().equals(medico.getId())) {
            throw new DuplicateResourceException("Ya existe un médico con el usuario ingresado");
        }

        // Actualizar datos del usuario
        usuario.setCorreo(dto.getCorreo());
        usuario.setPassword(dto.getPassword());
        usuarioRepository.save(usuario);

        // Actualizar datos del médico
        medico.setNombres(dto.getNombres());
        medico.setApellidos(dto.getApellidos());
        medico.setNumeroColegiatura(dto.getNumeroColegiatura());
        medico.setNumeroRNE(dto.getNumeroRNE());
        medico.setTelefono(dto.getTelefono());
        medico.setDireccion(dto.getDireccion());
        medico.setDescripcion(dto.getDescripcion());
        medico.setImagen(dto.getImagen());
        medico.setFechaContratacion(dto.getFechaContratacion());
        medico.setTipoContrato(dto.getTipoContrato());
        medico.setTipoMedico(dto.getTipoMedico());

        Medico actualizado = medicoRepository.save(medico);
        return medicoMapper.mapToMedicoResponseDTO(actualizado);
    }



    @Transactional
    @Override
    public void eliminarMedico(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Medico medico = medicoRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medico no encontrado con ID: " + id));
        medico.setEstado(false); // borrado logico
        Usuario usuario = usuarioRepository.findByIdAndEstadoIsTrue(medico.getUsuario().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        usuario.setEstado(false);
        medico.setUsuario(null);
        usuarioRepository.save(usuario);
        medicoRepository.save(medico);
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
