package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.AdministradorDTO;
import com.clinicaregional.clinica.dto.PacienteDTO;
import com.clinicaregional.clinica.dto.request.RegisterAdministradorRequest;
import com.clinicaregional.clinica.dto.request.RegisterRequest;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.UsuarioRequestDTO;
import com.clinicaregional.clinica.dto.response.AuthenticationResponseDTO;
import com.clinicaregional.clinica.dto.request.LoginRequestDTO;
import com.clinicaregional.clinica.mapper.UsuarioMapper;
import com.clinicaregional.clinica.service.AdministradorService;
import com.clinicaregional.clinica.service.AuthenticationService;
import com.clinicaregional.clinica.service.PacienteService;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.security.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final UsuarioMapper usuarioMapper;
    private final PacienteService pacienteService;
    private final AdministradorService administradorService;

    @Autowired
    public AuthenticationServiceImpl(JwtUtil jwtUtil, UsuarioService usuarioService, UserDetailsServiceImpl userDetailsServiceImpl, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder, PacienteService pacienteService, AdministradorService administradorService) {
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.pacienteService = pacienteService;
        this.administradorService = administradorService;
    }

    @Transactional(readOnly = true)
    @Override
    public AuthenticationResponseDTO authenticateUser(LoginRequestDTO loginRequestDTO) {
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(loginRequestDTO.getCorreo());
        System.out.println(userDetails);
        System.out.println(userDetails.getPassword());
        System.out.println(loginRequestDTO.getCorreo());
        System.out.println(loginRequestDTO.getPassword());
        if (passwordEncoder.matches(loginRequestDTO.getPassword(), userDetails.getPassword())) {
            // generamos token
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
            String jwtToken = jwtUtil.generateAccessToken(authentication);
            // generamos refresh token
            String refreshToken = jwtUtil.generateRefreshToken(authentication);
            // obtenemos al usuario
            Usuario usuario = usuarioService.obtenerPorCorreo(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            UsuarioDTO usuarioDTO = usuarioMapper.mapToUsuarioDTO(usuario);
            return usuarioMapper.mapToAuthenticationResponseDTO(usuarioDTO, jwtToken, refreshToken);
        } else {
            throw new BadCredentialsException("Credenciales incorrectas");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public String refreshToken(String refreshToken) {
        boolean isValid = jwtUtil.validateToken(refreshToken);
        if (isValid) {
            String email = jwtUtil.getEmailFromJwt(refreshToken);
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(email);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
            return jwtUtil.generateAccessToken(authentication);
        } else {
            throw new JwtException("Error al validad token de refresco");
        }
    }

    @Transactional
    @Override
    public AuthenticationResponseDTO registerPaciente(RegisterRequest registerRequest) {
        // Establecer el rol por defecto (Paciente)
        registerRequest.getUsuario().setRol(new RolDTO(1L, "PACIENTE"));

        UsuarioDTO usuarioGuardado = usuarioService.guardar(registerRequest.getUsuario());

        registerRequest.getPaciente().setUsuario(usuarioGuardado);

        PacienteDTO pacienteGuardado = pacienteService.crearPaciente(registerRequest.getPaciente());

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(usuarioGuardado.getCorreo());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());

        String jwtToken = jwtUtil.generateAccessToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(authentication);

        return usuarioMapper.mapToAuthenticationResponseDTO(usuarioGuardado, jwtToken, refreshToken);
    }

    @Transactional
    @Override
    public AuthenticationResponseDTO registerAdministrador(RegisterAdministradorRequest registerAdministradorRequest) {
        // Establecer el rol por defecto (ADMIN)
        registerAdministradorRequest.getUsuario().setRol(new RolDTO(2L, "ADMINISTRADOR"));

        UsuarioDTO usuarioGuardado = usuarioService.guardar(registerAdministradorRequest.getUsuario());

        registerAdministradorRequest.getAdministrador().setUsuarioId(usuarioGuardado.getId());

        AdministradorDTO savedAdministrador = administradorService.createAdministrador(registerAdministradorRequest.getAdministrador());

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(usuarioGuardado.getCorreo());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());

        String jwtToken = jwtUtil.generateAccessToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(authentication);

        return usuarioMapper.mapToAuthenticationResponseDTO(usuarioGuardado, jwtToken, refreshToken);
    }
}
