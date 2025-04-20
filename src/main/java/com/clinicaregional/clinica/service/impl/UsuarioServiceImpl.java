package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.UsuarioRequest;
import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.repository.RolRepository;
import com.clinicaregional.clinica.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioDTO> obtenerPorId(Long id) {
        return usuarioRepository.findById(id).map(this::convertirAUsuarioDTO);
    }

    @Override
    public Optional<Usuario> obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Override
    public List<UsuarioDTO> obtenerPorRol(Long rolId) {
        return usuarioRepository.findByRol_Id(rolId).stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioDTO guardar(UsuarioRequest request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new IllegalStateException("Ya existe un usuario con el correo ingresado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setContraseña(passwordEncoder.encode(request.getContraseña()));
        usuario.setEstado(request.isEstado());

        rolRepository.findById(request.getRol().getId())
                .ifPresentOrElse(usuario::setRol, () -> {
                    throw new IllegalStateException("El rol especificado no existe");
                });

        return convertirAUsuarioDTO(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioDTO actualizar(Long id, UsuarioRequest request) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNombre(request.getNombre());
            usuario.setCorreo(request.getCorreo());
            usuario.setContraseña(passwordEncoder.encode(request.getContraseña()));
            usuario.setEstado(request.isEstado());

            rolRepository.findById(request.getRol().getId())
                    .ifPresentOrElse(usuario::setRol, () -> {
                        throw new IllegalStateException("El rol especificado no existe");
                    });

            return convertirAUsuarioDTO(usuarioRepository.save(usuario));
        }).orElseThrow(() -> new IllegalStateException("El usuario no existe."));
    }

    @Override
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    // Conversión de entidad a DTO de salida
    private UsuarioDTO convertirAUsuarioDTO(Usuario usuario) {
        return new UsuarioDTO(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getCorreo(),
            usuario.isEstado(),
            usuario.getRol() != null ? new RolDTO(usuario.getRol().getId(), usuario.getRol().getNombre()) : null
        );
        
    }
}
