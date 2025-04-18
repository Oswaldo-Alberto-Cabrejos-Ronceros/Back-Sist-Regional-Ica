package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    public List<Usuario> listarUsuarios();

    public Optional<Usuario> obtenerPorId(Long id);

    public List<Usuario> obtenerPorRol(Long rolId);

    public Usuario guardar(Usuario usuario);

    public Usuario actualizar(Long id, Usuario nuevoUsuario);

    public void eliminar(Long id);
}