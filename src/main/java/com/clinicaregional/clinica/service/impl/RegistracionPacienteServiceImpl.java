package com.clinicaregional.clinica.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.clinicaregional.clinica.dto.PacienteConUserDTO;
import com.clinicaregional.clinica.dto.authentication.CompletePatientRegistrationRequest;
import com.clinicaregional.clinica.dto.authentication.VerifyCodeRequest;
import com.clinicaregional.clinica.dto.authentication.VerifyEmailRequest;
import com.clinicaregional.clinica.dto.authentication.VerifyPatientRequest;
import com.clinicaregional.clinica.dto.request.RegisterRequest;
import com.clinicaregional.clinica.dto.response.AuthenticationResponseDTO;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.exception.ValidationException;
import com.clinicaregional.clinica.mapper.PacienteMapper;
import com.clinicaregional.clinica.repository.PacienteRepository;
import com.clinicaregional.clinica.repository.RolRepository;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.security.JwtUtil;
import com.clinicaregional.clinica.service.AuthenticationService;
import com.clinicaregional.clinica.service.EmailVerificacionService;
import com.clinicaregional.clinica.service.PacienteService;
import com.clinicaregional.clinica.service.RegistracionPacienteService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RegistracionPacienteServiceImpl implements RegistracionPacienteService {

    private final PacienteService pacienteService;
    private final EmailVerificacionService emailVerificacionService;
    private final AuthenticationService authenticationService;
    private final PacienteMapper pacienteMapper;
    private final PacienteRepository pacienteRepository;
    private final RolRepository rolRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public RegistracionPacienteServiceImpl(
            PacienteService pacienteService,
            EmailVerificacionService emailVerificacionService,
            AuthenticationService authenticationService,
            PacienteMapper pacienteMapper,
            PacienteRepository pacienteRepository,
            RolRepository rolRepository,
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            UsuarioRepository usuarioRepository,
            JwtUtil jwtUtil) {

        this.pacienteService = pacienteService;
        this.emailVerificacionService = emailVerificacionService;
        this.authenticationService = authenticationService;
        this.pacienteMapper = pacienteMapper;
        this.pacienteRepository = pacienteRepository;
        this.rolRepository = rolRepository;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkPatientExists(VerifyPatientRequest request) {
        return pacienteService.getPacientePorIdentificacion(request.getDocumento())
                .isPresent();
    }

    @Override
    public void sendVerificationEmail(VerifyEmailRequest request) {
        // 1. Buscar paciente por email
        PacienteConUserDTO pacienteDTO = pacienteService.getPacientePorEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No existe un paciente registrado con este email"));

        // 2. Validar coincidencia documento-email
        if (!pacienteDTO.getNumeroIdentificacion().equals(request.getDocumento())) {
            throw new ValidationException("El email no está asociado al documento proporcionado");
        }

        // 3. Enviar código si todo es válido
        emailVerificacionService.sendVerificationCode(request.getEmail());
    }

    @Override
    public boolean verifyCode(VerifyCodeRequest request, String email) {
        return emailVerificacionService.verifyCode(email, request.getCode());
    }

    @Override
    @Transactional
    public AuthenticationResponseDTO completeExistingPatientRegistration(String documento,
            CompletePatientRegistrationRequest request) {
        try {
            // 1. Buscar paciente existente
            Paciente paciente = pacienteRepository.findByNumeroIdentificacion(documento)
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Paciente no encontrado con documento: " + documento));

            // 2. Verificar si ya tiene usuario
            if (paciente.getUsuario() != null) {
                throw new IllegalStateException("El paciente ya tiene una cuenta registrada");
            }

            // 3. Validar coincidencia de emails
            if (!request.getEmail().equals(paciente.getEmail())) {
                throw new ValidationException("El email no coincide con el registrado para este paciente");
            }

            // 4. Crear nuevo usuario
            Usuario usuario = new Usuario();
            usuario.setCorreo(request.getEmail());
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            usuario.setEstado(true);

            // 5. Asignar rol PACIENTE
            Rol rol = rolRepository.findByNombreAndEstadoTrue("PACIENTE")
                    .orElseThrow(() -> new IllegalStateException("Rol PACIENTE no encontrado"));
            usuario.setRol(rol);

            // 6. Guardar usuario
            usuario = usuarioRepository.save(usuario);
            log.info("Usuario registrado exitosamente - ID: {}, Email: {}", usuario.getId(), usuario.getCorreo());

            // 7. Forzar flush y refrescar entidad
            usuarioRepository.flush();
            usuario = usuarioRepository.findById(usuario.getId())
                    .orElseThrow(() -> new IllegalStateException("Error al recuperar usuario recién creado"));

            // 8. Asociar usuario al paciente
            paciente.setUsuario(usuario);
            pacienteRepository.save(paciente);

            // 9. Generar tokens de autenticación
            UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getCorreo());
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            String jwtToken = jwtUtil.generateAccessToken(authentication);
            String refreshToken = jwtUtil.generateRefreshToken(authentication);

            return new AuthenticationResponseDTO(
                    usuario.getId(),
                    rol.getNombre(),
                    jwtToken,
                    refreshToken);

        } catch (Exception e) {
            log.error("Error en completeExistingPatientRegistration para documento: " + documento, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public AuthenticationResponseDTO registerNewPatient(RegisterRequest request) {
        try {
            // 1. Validar que el documento no existe
            if (pacienteRepository.existsByNumeroIdentificacion(request.getNumeroDocumento())) {
                throw new ValidationException("Ya existe un paciente con este documento");
            }

            // 2. Crear un objeto RegisterRequest compatible con el AuthenticationService
            RegisterRequest compatibleRequest = new RegisterRequest();
            compatibleRequest.setEmail(request.getEmail());
            compatibleRequest.setPassword(request.getPassword());
            compatibleRequest.setNombres(request.getNombres());
            compatibleRequest.setApellidos(request.getApellidos());
            compatibleRequest.setFechaNacimiento(request.getFechaNacimiento());
            compatibleRequest.setSexo(request.getSexo());
            compatibleRequest.setTipoDocumentoId(request.getTipoDocumentoId());
            compatibleRequest.setNumeroDocumento(request.getNumeroDocumento());
            compatibleRequest.setTelefono(request.getTelefono());
            compatibleRequest.setDireccion(request.getDireccion());
            compatibleRequest.setSeguroId(request.getSeguroId());
            compatibleRequest.setNumeroPoliza(request.getNumeroPoliza());
            compatibleRequest.setModalidadAtencion(request.getModalidadAtencion());
            compatibleRequest.setContactoEmergenciaNombre(request.getContactoEmergenciaNombre());
            compatibleRequest.setContactoEmergenciaTelefono(request.getContactoEmergenciaTelefono());

            // 3. Usar el servicio de autenticación
            return authenticationService.registerPaciente(compatibleRequest);

        } catch (Exception e) {
            log.error("Error al registrar nuevo paciente", e);
            throw e;
        }
    }
}