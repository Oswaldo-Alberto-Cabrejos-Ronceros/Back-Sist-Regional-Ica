package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.entity.Rol;

import java.util.List;
import java.util.Optional;

public interface RolService {
    public List<RolDTO> listarRoles();

    public Optional<RolDTO> obtenerPorId(Long id);

    public RolDTO guardar(RolDTO rolDTO);

    public RolDTO actualizar(Long id, RolDTO nuevoRol);

    public void eliminar(Long id);
}