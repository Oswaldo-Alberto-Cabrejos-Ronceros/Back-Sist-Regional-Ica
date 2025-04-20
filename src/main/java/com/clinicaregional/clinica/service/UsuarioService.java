package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.UsuarioRequest;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    public List<UsuarioDTO> listarUsuarios();


    Optional<UsuarioDTO> obtenerPorId(Long id);

    Optional<Usuario> obtenerPorCorreo(String correo); // para autenticación interna

    List<UsuarioDTO> obtenerPorRol(Long rolId);

    UsuarioDTO guardar(UsuarioRequest usuarioRequestDTO); // cambia DTO por RequestDTO

    UsuarioDTO actualizar(Long id, UsuarioRequest usuarioRequestDTO); // idem

    void eliminar(Long id);
}