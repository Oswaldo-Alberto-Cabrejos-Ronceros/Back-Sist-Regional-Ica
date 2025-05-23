package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.response.AuthenticationResponseDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.UsuarioRequestDTO;
import com.clinicaregional.clinica.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    private final RolMapper rolMapper;
    public UsuarioMapper(RolMapper rolMapper) {
        this.rolMapper = rolMapper;
    }

    public UsuarioDTO mapToUsuarioDTO(Usuario usuario) {
        return new UsuarioDTO(usuario.getId(), usuario.getCorreo(), rolMapper.mapToRolDTO(usuario.getRol()));
    }

    public Usuario mapToUsuario(UsuarioDTO usuarioDTO) {
        return new Usuario(usuarioDTO.getId(), usuarioDTO.getCorreo(), null, rolMapper.mapToRol(usuarioDTO.getRol()));
    }

    public AuthenticationResponseDTO mapToAuthenticationResponseDTO(UsuarioDTO usuarioDTO, String jwtToken, String refreshToken) {
        return new AuthenticationResponseDTO(usuarioDTO.getId(), usuarioDTO.getRol().getNombre(), jwtToken, refreshToken);
    }

    public Usuario mapFromUsuarioRequestDTOToUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        return new Usuario(null,usuarioRequestDTO.getCorreo(),usuarioRequestDTO.getPassword(),rolMapper.mapToRol(usuarioRequestDTO.getRol()));
    }

}
