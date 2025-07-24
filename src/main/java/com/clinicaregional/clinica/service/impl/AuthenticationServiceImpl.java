package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.PacienteConUserDTO;

import com.clinicaregional.clinica.dto.request.RegisterRequest;
import com.clinicaregional.clinica.entity.*;
import com.clinicaregional.clinica.exception.*;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.response.AuthenticationResponseDTO;
import com.clinicaregional.clinica.dto.request.LoginRequestDTO;
import com.clinicaregional.clinica.mapper.PacienteMapper;
import com.clinicaregional.clinica.mapper.UsuarioMapper;
import com.clinicaregional.clinica.repository.*;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.*;
import com.clinicaregional.clinica.service.impl.email.RegistroCompletoEmailService;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final PacienteMapper pacienteMapper;
    private final JavaMailSender mailSender;
    private final RegistroCompletoEmailService emailRegistroService;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final SeguroRepository seguroRepository;

    @Autowired
    public AuthenticationServiceImpl(
            JwtUtil jwtUtil,
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            UsuarioMapper usuarioMapper,
            PacienteMapper pacienteMapper,
            JavaMailSender mailSender,
            RegistroCompletoEmailService emailRegistroService,
            PacienteRepository pacienteRepository,
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            TipoDocumentoRepository tipoDocumentoRepository,
            SeguroRepository seguroRepository) {

        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
        this.pacienteMapper = pacienteMapper;
        this.mailSender = mailSender;
        this.emailRegistroService = emailRegistroService;
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.seguroRepository = seguroRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public AuthenticationResponseDTO authenticateUser(LoginRequestDTO loginRequestDTO) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDTO.getCorreo());
        log.debug("Contraseña ingresada (plaintext): '{}'", loginRequestDTO.getPassword());
        log.debug("Contraseña de la BD (hashed): '{}'", userDetails.getPassword());
        log.debug("¿Coincide?: {}", passwordEncoder.matches(loginRequestDTO.getPassword(), userDetails.getPassword()));

        if (passwordEncoder.matches(loginRequestDTO.getPassword(), userDetails.getPassword())) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    null,
                    userDetails.getAuthorities());

            String jwtToken = jwtUtil.generateAccessToken(authentication);
            String refreshToken = jwtUtil.generateRefreshToken(authentication);

            Usuario usuario = usuarioRepository.findByCorreoAndEstadoIsTrue(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado o inactivo"));

            return new AuthenticationResponseDTO(
                    usuario.getId(),
                    usuario.getRol().getNombre(),
                    jwtToken,
                    refreshToken);
        } else {
            throw new BadCredentialsException("Credenciales incorrectas");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public String refreshToken(String refreshToken) {
        if (jwtUtil.validateToken(refreshToken)) {
            String email = jwtUtil.getEmailFromJwt(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    null,
                    userDetails.getAuthorities());
            return jwtUtil.generateAccessToken(authentication);
        }
        throw new JwtException("Error al validar token de refresco");
    }

    @Override
    @Transactional
    public AuthenticationResponseDTO registerPaciente(RegisterRequest request) {
        try {
            // Validaciones básicas
            if (request == null) {
                throw new IllegalArgumentException("La solicitud de registro no puede ser nula");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
            }

            log.info("Registrando nuevo paciente con documento: {}", request.getNumeroDocumento());

            // Validar duplicados
            if (pacienteRepository.existsByNumeroIdentificacion(request.getNumeroDocumento())) {
                throw new DuplicateResourceException("Ya existe un paciente con este número de documento");
            }
            if (usuarioRepository.existsByCorreo(request.getEmail())) {
                throw new DuplicateResourceException("El email ya está registrado");
            }

            // Obtener entidades relacionadas
            Rol rolPaciente = rolRepository.findByNombreAndEstadoTrue("PACIENTE")
                    .orElseThrow(() -> new ResourceNotFoundException("Rol PACIENTE no encontrado"));

            TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(request.getTipoDocumentoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo de documento no encontrado"));

            Seguro seguro = request.getSeguroId() != null ? seguroRepository.findById(request.getSeguroId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seguro no encontrado")) : null;

            // Crear y guardar usuario
            Usuario usuario = new Usuario();
            usuario.setCorreo(request.getEmail());
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            usuario.setRol(rolPaciente);
            usuario.setEstado(true);
            usuario = usuarioRepository.save(usuario);

            // Crear y guardar paciente
            Paciente paciente = new Paciente();
            paciente.setNombres(request.getNombres());
            paciente.setApellidos(request.getApellidos());
            paciente.setFechaNacimiento(request.getFechaNacimiento());
            paciente.setSexo(request.getSexo());
            paciente.setEmail(request.getEmail());
            paciente.setTipoDocumento(tipoDocumento);
            paciente.setNumeroIdentificacion(request.getNumeroDocumento());
            paciente.setTelefono(request.getTelefono());
            paciente.setDireccion(request.getDireccion());
            paciente.setModalidadDeAtencion(request.getModalidadAtencion());
            paciente.setSeguro(seguro);
            paciente.setNumeroDePoliza(request.getNumeroPoliza());
            paciente.setContactoDeEmergenciaNombre(request.getContactoEmergenciaNombre());
            paciente.setContactoDeEmergenciaTelefono(request.getContactoEmergenciaTelefono());
            paciente.setUsuario(usuario);
            paciente.setEstado(true);
            Paciente pacienteGuardado = pacienteRepository.save(paciente);

            // Generar tokens
            UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getCorreo());
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            String jwtToken = jwtUtil.generateAccessToken(authentication);
            String refreshToken = jwtUtil.generateRefreshToken(authentication);

            // Enviar email de confirmación
            try {
                enviarEmailConfirmacion(
                        pacienteMapper.mapToPacienteDTO(pacienteGuardado),
                        usuarioMapper.mapToUsuarioDTO(usuario));
                log.info("Email de confirmación enviado a: {}", usuario.getCorreo());
            } catch (EmailSendingException e) {
                log.error("Error al enviar email de confirmación", e);
            }

            return new AuthenticationResponseDTO(
                    usuario.getId(),
                    rolPaciente.getNombre(),
                    jwtToken,
                    refreshToken);

        } catch (Exception e) {
            log.error("Error en el registro de paciente", e);
            throw e;
        }
    }

    private void enviarEmailConfirmacion(PacienteConUserDTO paciente, UsuarioDTO usuario) {
        try {
            emailRegistroService.enviarConfirmacionRegistro(paciente, usuario);
        } catch (EmailSendingException e) {
            log.error("Error al enviar email de confirmación: {}", e.getMessage());
        }
    }
}