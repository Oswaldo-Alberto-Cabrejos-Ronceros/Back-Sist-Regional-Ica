package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.mapper.RolMapper;
import com.clinicaregional.clinica.repository.RolRepository;
import com.clinicaregional.clinica.service.RolService;

import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RolServiceImpl extends FiltroEstado implements RolService {

    private final RolRepository rolRepository;
    private final RolMapper rolMapper;

    @Autowired
    public RolServiceImpl(RolRepository rolRepository, RolMapper rolMapper) {
        this.rolRepository = rolRepository;
        this.rolMapper = rolMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RolDTO> listarRoles() {
        activarFiltroEstado(true);
        return rolRepository.findAll().stream().map(rolMapper::mapToRolDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<RolDTO> obtenerPorId(Long id) {
        activarFiltroEstado(true);
        return rolRepository.findByIdAndEstadoIsTrue(id).map(rolMapper::mapToRolDTO);
    }

    @Transactional
    @Override
    public RolDTO guardar(RolDTO rolDTO) {
        activarFiltroEstado(true);
        if (rolRepository.existsByNombre(rolDTO.getNombre())) {
            throw new IllegalArgumentException("El nombre ya existe");
        }
        Rol rol = rolMapper.mapToRol(rolDTO);
        Rol savedRol = rolRepository.save(rol);
        return rolMapper.mapToRolDTO(savedRol);
    }

    @Transactional
    @Override
    public RolDTO actualizar(Long id, RolDTO Rol) {
        activarFiltroEstado(true);
        Rol rolExisting = rolRepository.findById(id).orElseThrow(() -> new RuntimeException("No existe un rol con el id" + id));
        rolExisting.setNombre(Rol.getNombre());
        rolExisting.setDescripcion(Rol.getDescripcion());
        Rol savedRol = rolRepository.save(rolExisting);
        return rolMapper.mapToRolDTO(savedRol);
    }

    @Transactional
    @Override
    public void eliminar(Long id) {
        activarFiltroEstado(true);
        Rol rol = rolRepository.findById(id).orElseThrow(()->new RuntimeException("No existe un rol con el id" + id));
        rol.setEstado(false); //borrado lógico
        rolRepository.save(rol);
    }
}