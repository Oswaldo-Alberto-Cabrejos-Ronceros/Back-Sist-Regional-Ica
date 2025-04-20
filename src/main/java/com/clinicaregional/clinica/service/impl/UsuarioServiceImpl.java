package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.UsuarioRequestDTO;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.mapper.UsuarioMapper;
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

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Override
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::mapToUsuarioDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioDTO> obtenerPorId(Long id) {
        return usuarioRepository.findById(id).map(usuarioMapper::mapToUsuarioDTO);
    }

    @Override
    public Optional<Usuario> obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Override
    public List<UsuarioDTO> obtenerPorRol(Long rolId) {
        return usuarioRepository.findByRol_Id(rolId).stream()
                .map(usuarioMapper::mapToUsuarioDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioDTO guardar(UsuarioRequestDTO request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new IllegalStateException("Ya existe un usuario con el correo ingresado");
        }

        Usuario usuario = usuarioMapper.mapFromUsuarioRequestDTOToUsuario(request);

        //hasheamos la contraseña

        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));

        rolRepository.findById(request.getRol().getId())
                .ifPresentOrElse(usuario::setRol, () -> {
                    throw new IllegalStateException("El rol especificado no existe");
                });

        return usuarioMapper.mapToUsuarioDTO(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioDTO actualizar(Long id, UsuarioRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(()->new RuntimeException("No existe un usuario con el id:" + id));

        //hasheamos la contraseña

        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));

        rolRepository.findById(request.getRol().getId())
                .ifPresentOrElse(usuario::setRol, () -> {
                    throw new IllegalStateException("El rol especificado no existe");
                });
        Usuario usuarioSaved = usuarioRepository.save(usuario);
        return usuarioMapper.mapToUsuarioDTO(usuarioSaved);
    }

    @Override
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    }

