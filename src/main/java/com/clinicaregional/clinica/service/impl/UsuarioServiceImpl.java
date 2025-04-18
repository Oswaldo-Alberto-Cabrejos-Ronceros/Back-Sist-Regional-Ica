package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.repository.RolRepository;
import com.clinicaregional.clinica.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public List<Usuario> obtenerPorRol(Long rolId) {
        return usuarioRepository.findByRol_Id(rolId);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        if (usuario.getId() != null && usuarioRepository.existsById(usuario.getId())) {
            throw new IllegalStateException("El usuario ya existe y no puede ser creado nuevamente.");
        }
        if (usuario.getRol() != null && usuario.getRol().getId() != null) {
            rolRepository.findById(usuario.getRol().getId()).ifPresent(usuario::setRol);
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario actualizar(Long id, Usuario nuevoUsuario) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNombre(nuevoUsuario.getNombre());
            usuario.setCorreo(nuevoUsuario.getCorreo());
            usuario.setContraseña(nuevoUsuario.getContraseña());
            usuario.setEstado(nuevoUsuario.isEstado());
            if (nuevoUsuario.getRol() != null && nuevoUsuario.getRol().getId() != null) {
                rolRepository.findById(nuevoUsuario.getRol().getId()).ifPresent(usuario::setRol);
            }
            return usuarioRepository.save(usuario);
        }).orElseThrow(() -> new IllegalStateException("El usuario no existe."));
    }

    @Override
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
}