package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
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
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final RolMapper rolMapper;
    private final FiltroEstado filtroEstado;

    @Autowired
    public RolServiceImpl(RolRepository rolRepository, RolMapper rolMapper, FiltroEstado filtroEstado) {
        this.rolRepository = rolRepository;
        this.rolMapper = rolMapper;
        this.filtroEstado = filtroEstado;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RolDTO> listarRoles() {
        filtroEstado.activarFiltroEstado(true);
        return rolRepository.findAll().stream().map(rolMapper::mapToRolDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<RolDTO> obtenerPorId(Long id) {
        filtroEstado.activarFiltroEstado(true);
        return rolRepository.findByIdAndEstadoIsTrue(id).map(rolMapper::mapToRolDTO);
    }

    @Transactional
    @Override
    public RolDTO guardar(RolDTO rolDTO) {
        filtroEstado.activarFiltroEstado(true);

        if (rolRepository.existsByNombreAndEstadoIsTrue(rolDTO.getNombre())) {
            throw new DuplicateResourceException("Ya existe un rol con el nombre ingresado");
        }

        Rol rol = rolMapper.mapToRol(rolDTO);
        Rol savedRol = rolRepository.save(rol);
        return rolMapper.mapToRolDTO(savedRol);
    }

    @Transactional
    @Override
    public RolDTO actualizar(Long id, RolDTO rol) {
        filtroEstado.activarFiltroEstado(true);

        Rol rolExisting = rolRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un rol con el id: " + id));

        boolean nombreDuplicado = rolRepository.existsByNombreAndEstadoIsTrue(rol.getNombre())
                && !rolExisting.getNombre().equalsIgnoreCase(rol.getNombre());

        if (nombreDuplicado) {
            throw new DuplicateResourceException("Ya existe un rol con el nombre ingresado");
        }

        rolExisting.setNombre(rol.getNombre());
        rolExisting.setDescripcion(rol.getDescripcion());
        Rol savedRol = rolRepository.save(rolExisting);
        return rolMapper.mapToRolDTO(savedRol);
    }

    @Transactional
    @Override
    public void eliminar(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Rol rol = rolRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new RuntimeException("No existe un rol con el id" + id));
        rol.setEstado(false); // borrado lógico
        rolRepository.save(rol);
    }
}
