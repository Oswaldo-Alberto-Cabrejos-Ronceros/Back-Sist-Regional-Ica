package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.entity.Rol;
import org.springframework.stereotype.Component;

@Component
public class RolMapper {
    public RolDTO mapToRolDTO(Rol rol) {
        return new RolDTO(rol.getId(), rol.getNombre(), rol.getDescripcion());
    }

    public Rol mapToRol(RolDTO rolDTO) {
        return new Rol(rolDTO.getId(), rolDTO.getNombre(), rolDTO.getDescripcion());
    }

}
