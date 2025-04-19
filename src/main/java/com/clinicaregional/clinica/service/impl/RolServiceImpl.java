package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.repository.RolRepository;
import com.clinicaregional.clinica.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    @Override
    public Optional<Rol> obtenerPorId(Long id) {
        return rolRepository.findById(id);
    }

    @Override
    public Rol guardar(Rol rol) {
        return rolRepository.save(rol);
    }

    @Override
    public Rol actualizar(Long id, Rol nuevoRol) {
        return rolRepository.findById(id).map(rol -> {
            rol.setNombre(nuevoRol.getNombre());
            rol.setDescripcion(nuevoRol.getDescripcion());
            return rolRepository.save(rol);
        }).orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        rolRepository.deleteById(id);
    }
}