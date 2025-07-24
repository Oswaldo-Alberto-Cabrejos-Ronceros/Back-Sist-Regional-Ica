package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.UsuarioRequestDTO;
import com.clinicaregional.clinica.entity.*;
import com.clinicaregional.clinica.exception.BadRequestException;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.UsuarioMapper;
import com.clinicaregional.clinica.repository.*;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.util.FiltroEstado;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final RecepcionistaRepository recepcionistaRepository;
    private final AdministradorRepository administradorRepository;
    private final FiltroEstado filtroEstado;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository,
            PasswordEncoder passwordEncoder, UsuarioMapper usuarioMapper,
            PacienteRepository pacienteRepository, MedicoRepository medicoRepository,
            RecepcionistaRepository recepcionistaRepository,
            AdministradorRepository administradorRepository, FiltroEstado filtroEstado) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.recepcionistaRepository = recepcionistaRepository;
        this.administradorRepository = administradorRepository;
        this.filtroEstado = filtroEstado;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UsuarioDTO> listarUsuarios() {
        filtroEstado.activarFiltroEstado(true);
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::mapToUsuarioDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UsuarioDTO> obtenerPorId(Long id) {
        filtroEstado.activarFiltroEstado(true);
        return usuarioRepository.findByIdAndEstadoIsTrue(id)
                .map(usuarioMapper::mapToUsuarioDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Usuario> obtenerPorIdContenxt(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Usuario> obtenerPorCorreo(String correo) {
        filtroEstado.activarFiltroEstado(true);
        log.debug("Buscando usuario por correo: {}", correo);

        // Asegúrate de que esta consulta incluya la contraseña
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreoAndEstadoIsTrue(correo);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            log.debug("Usuario encontrado - ID: {}, Password null? {}",
                    usuario.getId(), usuario.getPassword() == null);
        } else {
            log.debug("No se encontró usuario con correo: {}", correo);
        }

        return usuarioOpt;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UsuarioDTO> obtenerPorRol(Long rolId) {
        filtroEstado.activarFiltroEstado(true);
        return usuarioRepository.findByRol_Id(rolId)
                .stream()
                .map(usuarioMapper::mapToUsuarioDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UsuarioDTO guardar(UsuarioRequestDTO request) {
        filtroEstado.activarFiltroEstado(true);

        // Validación adicional
        if (request.getCorreo() == null || request.getCorreo().isEmpty()) {
            throw new BadRequestException("El correo es obligatorio");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new BadRequestException("La contraseña es obligatoria");
        }
        if (request.getRol() == null || request.getRol().getId() == null) {
            throw new BadRequestException("El rol es obligatorio");
        }

        if (usuarioRepository.existsByCorreoAndEstadoIsTrue(request.getCorreo())) {
            throw new DuplicateResourceException("Ya existe un usuario con el correo ingresado");
        }

        Usuario usuario = usuarioMapper.mapFromUsuarioRequestDTOToUsuario(request);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        Rol rol = rolRepository.findById(request.getRol().getId())
                .orElseThrow(() -> new BadRequestException("El rol especificado no existe"));
        usuario.setRol(rol);

        // Asegurar que el estado esté activo
        usuario.setEstado(true);

        Usuario usuarioSaved = usuarioRepository.save(usuario);
        return usuarioMapper.mapToUsuarioDTO(usuarioSaved);
    }

    @Transactional
    @Override
    public UsuarioDTO actualizar(Long id, UsuarioRequestDTO request) {
        filtroEstado.activarFiltroEstado(true);

        Usuario usuario = usuarioRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario con el id: " + id));

        // Verifica si el correo se modificó y si ya existe otro usuario con ese correo
        boolean correoDuplicado = usuarioRepository.existsByCorreoAndEstadoIsTrue(request.getCorreo())
                && !usuario.getCorreo().equalsIgnoreCase(request.getCorreo());

        if (correoDuplicado) {
            throw new DuplicateResourceException("Ya existe un usuario con el correo ingresado");
        }

        usuario.setCorreo(request.getCorreo());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        Rol rol = rolRepository.findById(request.getRol().getId())
                .orElseThrow(() -> new BadRequestException("El rol especificado no existe"));

        usuario.setRol(rol);

        Usuario usuarioSaved = usuarioRepository.save(usuario);
        return usuarioMapper.mapToUsuarioDTO(usuarioSaved);
    }

    @Transactional
    @Override
    public void eliminar(Long id) {
        filtroEstado.activarFiltroEstado(true);

        Usuario usuario = usuarioRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario con el id: " + id));

        usuario.setEstado(false); // Borrado lógico
        usuarioRepository.save(usuario);

        switch (usuario.getRol().getNombre()) {
            case "ADMIN":
                Administrador administrador = administradorRepository.findByUsuario_Id(usuario.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Administrador no existe con el id de usuario ingresado"));
                administrador.setUsuario(null);
                administrador.setEstado(false);
                administradorRepository.save(administrador);
                break;
            case "PACIENTE":
                Paciente paciente = pacienteRepository.findByUsuario_Id(usuario.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Paciente no existe con el id de usuario ingresado"));
                paciente.setUsuario(null);
                paciente.setEstado(false);
                pacienteRepository.save(paciente);
                break;
            case "MEDICO":
                Medico medico = medicoRepository.findByUsuario_Id(usuario.getId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Médico no existe con el id de usuario ingresado"));
                medico.setUsuario(null);
                medico.setEstado(false);
                medicoRepository.save(medico);
                break;
            case "RECEPCIONISTA":
                Recepcionista recepcionista = recepcionistaRepository.findByUsuario_Id(usuario.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Recepcionista no existe con el id de usuario ingresado"));
                recepcionista.setUsuario(null);
                recepcionista.setEstado(false);
                recepcionistaRepository.save(recepcionista);
                break;
            default:
                throw new BadRequestException("Rol no manejado: " + usuario.getRol().getNombre());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void eliminarUsuarioSinRelaciones(Long usuarioId) {
        Usuario usuario = usuarioRepository.findByIdAndEstadoIsTrue(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Solo marca el usuario como inactivo sin tocar las relaciones
        usuario.setEstado(false);
        usuarioRepository.save(usuario);
    }
}
