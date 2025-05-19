package com.clinicaregional.clinica.service.impl;

import java.util.stream.Collectors;
import java.util.List;

import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
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
import com.clinicaregional.clinica.mapper.MedicoMapper;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.service.MedicoService;
import com.clinicaregional.clinica.service.UsuarioService;

import org.springframework.transaction.annotation.Transactional;

@Service
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final FiltroEstado filtroEstado;

    @Autowired
    public MedicoServiceImpl(
            MedicoRepository medicoRepository,
            MedicoMapper medicoMapper,
            UsuarioRepository usuarioRepository,
            UsuarioService usuarioService,
            FiltroEstado filtroEstado) {
        this.medicoRepository = medicoRepository;
        this.medicoMapper = medicoMapper;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
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

    @Transactional
    @Override
    public MedicoResponseDTO guardarMedico(MedicoRequestDTO dto) {
        filtroEstado.activarFiltroEstado(true);

        if (medicoRepository.existsByNumeroColegiatura(dto.getNumeroColegiatura())) {
            throw new RuntimeException("Ya existe un médico con el número de colegiatura ingresado");
        }

        // Validar RNE solo si es ESPECIALISTA
        if (dto.getTipoMedico().name().equals("ESPECIALISTA")) {
            if (dto.getNumeroRNE() == null || dto.getNumeroRNE().isBlank()) {
                throw new RuntimeException("El número RNE es obligatorio para médicos especialistas");
            }
            if (medicoRepository.existsByNumeroRNE(dto.getNumeroRNE())) {
                throw new RuntimeException("Ya existe un médico con el RNE ingresado");
            }
        } else {
            dto.setNumeroRNE(null); // limpiar por si se envió accidentalmente
        }

        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new RuntimeException("Ya existe un usuario con el correo ingresado");
        }

        UsuarioRequestDTO newUsuario = new UsuarioRequestDTO();
        newUsuario.setCorreo(dto.getCorreo());
        newUsuario.setPassword(dto.getPassword());
        RolDTO rolMedico = new RolDTO();
        rolMedico.setId(4L); // ID del rol de médico
        newUsuario.setRol(rolMedico);
        UsuarioDTO usuarioDTO = usuarioService.guardar(newUsuario);

        Usuario usuario1 = new Usuario();
        usuario1.setId(usuarioDTO.getId());

        Medico medico = Medico.builder()
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .numeroColegiatura(dto.getNumeroColegiatura())
                .numeroRNE(dto.getNumeroRNE())
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

        Usuario usuario = usuarioRepository.findByIdAndEstadoIsTrue(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getUsuarioId()));

        if (medicoRepository.existsByNumeroColegiaturaAndIdNot(dto.getNumeroColegiatura(), id)) {
            throw new DuplicateResourceException("Ya existe un médico con el número de colegiatura ingresado");
        }

        if (dto.getNumeroRNE() != null &&
                medicoRepository.existsByNumeroRNEAndIdNot(dto.getNumeroRNE(), id)) {
            throw new DuplicateResourceException("Ya existe un médico con el RNE ingresado");
        }

        if (medicoRepository.existsByUsuarioAndIdNot(usuario, id)) {
            throw new DuplicateResourceException("Ya existe un médico con el usuario ingresado");
        }

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
        medico.setUsuario(usuario);

        Medico actualizado = medicoRepository.save(medico);
        return medicoMapper.mapToMedicoResponseDTO(actualizado);
    }


    @Transactional
    @Override
    public void eliminarMedico(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Medico medico = medicoRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new RuntimeException("Medico no encontrado con ID: " + id));
        medico.setEstado(false); // borrado logico
        Usuario usuario = usuarioRepository.findByIdAndEstadoIsTrue(medico.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setEstado(false);
        medico.setUsuario(null);
        usuarioRepository.save(usuario);
        medicoRepository.save(medico);
    }
}
