package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.UsuarioRequestDTO;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.mapper.UsuarioMapper;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.repository.RolRepository;
import com.clinicaregional.clinica.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream().map(usuarioMapper::mapToUsuarioDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UsuarioDTO> obtenerPorId(Long id) {
        return usuarioRepository.findById(id).map(usuarioMapper::mapToUsuarioDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Usuario> obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UsuarioDTO> obtenerPorRol(Long rolId) {
        return usuarioRepository.findByRol_Id(rolId).stream().map(usuarioMapper::mapToUsuarioDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UsuarioDTO guardar(UsuarioRequestDTO request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new IllegalStateException("Ya existe un usuario con el correo ingresado");
        }

        Usuario usuario = usuarioMapper.mapFromUsuarioRequestDTOToUsuario(request);

        //hasheamos la contraseña

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        rolRepository.findById(request.getRol().getId()).ifPresentOrElse(usuario::setRol, () -> {
            throw new IllegalStateException("El rol especificado no existe");
        });

        return usuarioMapper.mapToUsuarioDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    @Override
    public UsuarioDTO actualizar(Long id, UsuarioRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("No existe un usuario con el id:" + id));

        //hasheamos la contraseña

        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        rolRepository.findById(request.getRol().getId()).ifPresentOrElse(usuario::setRol, () -> {
            throw new IllegalStateException("El rol especificado no existe");
        });
        Usuario usuarioSaved = usuarioRepository.save(usuario);
        return usuarioMapper.mapToUsuarioDTO(usuarioSaved);
    }

    @Transactional
    @Override
    public void eliminar(Long id) {
        usuarioRepository.findById(id).ifPresentOrElse(
            u -> usuarioRepository.deleteById(id),
            () -> { throw new RuntimeException("Usuario no encontrado"); }
        );
    }
    
}

