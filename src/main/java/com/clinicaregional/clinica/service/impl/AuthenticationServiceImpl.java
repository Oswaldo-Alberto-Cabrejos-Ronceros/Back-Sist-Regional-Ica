package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.UsuarioRequestDTO;
import com.clinicaregional.clinica.dto.AuthenticationResponseDTO;
import com.clinicaregional.clinica.dto.LoginRequestDTO;
import com.clinicaregional.clinica.mapper.UsuarioMapper;
import com.clinicaregional.clinica.service.AuthenticationService;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.security.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final UsuarioMapper usuarioMapper;

    @Autowired
    public AuthenticationServiceImpl(JwtUtil jwtUtil, UsuarioService usuarioService, UserDetailsServiceImpl userDetailsServiceImpl,UsuarioMapper usuarioMapper) {
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public AuthenticationResponseDTO authenticateUser(LoginRequestDTO loginRequestDTO) {
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(loginRequestDTO.getEmail());
        System.out.println(userDetails);
        System.out.println(userDetails.getPassword());
        System.out.println(loginRequestDTO.getEmail());
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

    @Override
    public AuthenticationResponseDTO registerUser(UsuarioRequestDTO usuarioRequestDTO) {
        // Establecer el rol por defecto (Paciente)
        usuarioRequestDTO.setRol(new RolDTO(1L, "Paciente"));

        UsuarioDTO usuarioGuardado = usuarioService.guardar(usuarioRequestDTO);

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(usuarioGuardado.getCorreo());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());

        String jwtToken = jwtUtil.generateAccessToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(authentication);

        return usuarioMapper.mapToAuthenticationResponseDTO(usuarioGuardado, jwtToken, refreshToken);
    }


}
