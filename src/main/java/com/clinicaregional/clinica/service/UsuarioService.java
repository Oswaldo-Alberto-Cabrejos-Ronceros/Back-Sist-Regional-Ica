package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.UsuarioRequestDTO;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<UsuarioDTO> listarUsuarios();

    Optional<UsuarioDTO> obtenerPorId(Long id);

    //para mantener contexto

    Optional<Usuario> obtenerPorIdContenxt(Long id);

    Optional<Usuario> obtenerPorCorreo(String correo); // para autenticación interna

    List<UsuarioDTO> obtenerPorRol(Long rolId);

    UsuarioDTO guardar(UsuarioRequestDTO usuarioRequestDTO); // cambia DTO por RequestDTO

    UsuarioDTO actualizar(Long id, UsuarioRequestDTO usuarioRequestDTO); // idem

    void eliminar(Long id);

    void eliminarUsuarioSinRelaciones(Long usuarioId);
}