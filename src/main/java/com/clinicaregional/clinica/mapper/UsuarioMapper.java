package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.response.AuthenticationResponseDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.UsuarioRequestDTO;
import com.clinicaregional.clinica.entity.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    private final RolMapper rolMapper;
    private final PasswordEncoder passwordEncoder; // Añade esto

    @Autowired // Añade el autowired si no lo tienes
    public UsuarioMapper(RolMapper rolMapper, PasswordEncoder passwordEncoder) {
        this.rolMapper = rolMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioDTO mapToUsuarioDTO(Usuario usuario) {
        return new UsuarioDTO(usuario.getId(), usuario.getCorreo(), rolMapper.mapToRolDTO(usuario.getRol()));
    }

    public Usuario mapToUsuario(UsuarioDTO usuarioDTO) {
        // No establecer password como null
        return new Usuario(
                usuarioDTO.getId(),
                usuarioDTO.getCorreo(),
                "", // O mantenerlo null si es apropiado
                rolMapper.mapToRol(usuarioDTO.getRol()));
    }

    public AuthenticationResponseDTO mapToAuthenticationResponseDTO(UsuarioDTO usuarioDTO, String jwtToken,
            String refreshToken) {
        return new AuthenticationResponseDTO(
                usuarioDTO.getId(),
                usuarioDTO.getRol() != null ? usuarioDTO.getRol().getNombre() : null,
                jwtToken,
                refreshToken);
    }

    public Usuario mapFromUsuarioRequestDTOToUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        return new Usuario(
                null,
                usuarioRequestDTO.getCorreo(),
                usuarioRequestDTO.getPassword(),
                usuarioRequestDTO.getRol() != null ? rolMapper.mapToRol(usuarioRequestDTO.getRol()) : null);
    }

}