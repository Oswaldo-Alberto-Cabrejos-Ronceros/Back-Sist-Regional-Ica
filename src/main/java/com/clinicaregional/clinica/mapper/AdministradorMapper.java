package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.AdministradorDTO;
import com.clinicaregional.clinica.entity.Administrador;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class AdministradorMapper {
    public AdministradorDTO mapToAdministradorDTO(Administrador administrador) {
        return new AdministradorDTO(administrador.getId(), administrador.getNombres(),
                administrador.getApellidos(), administrador.getNumeroDocumento(),
                administrador.getTipoDocumento().getId(), administrador.getTelefono(),
                administrador.getDireccion(), administrador.getFechaContratacion(),
                administrador.getUsuario().getId());
    }

    public Administrador mapToAdministrador(AdministradorDTO administradorDTO) {
        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setId(administradorDTO.getTipoDocumentoId());

        Usuario usuario = new Usuario();
        usuario.setId(administradorDTO.getUsuarioId());

        return new Administrador(administradorDTO.getId(), administradorDTO.getNombres(), administradorDTO.getApellidos(), administradorDTO.getNumeroDocumento(), tipoDocumento, administradorDTO.getTelefono(), administradorDTO.getDireccion(), administradorDTO.getFechaContratacion(), usuario);
    }
}
