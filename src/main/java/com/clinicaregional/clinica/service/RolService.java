package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.entity.Rol;

import java.util.List;
import java.util.Optional;

public interface RolService {
    public List<Rol> listarRoles();

    public Optional<Rol> obtenerPorId(Long id);

    public Rol guardar(Rol rol);

    public Rol actualizar(Long id, Rol nuevoRol);

    public void eliminar(Long id);
}