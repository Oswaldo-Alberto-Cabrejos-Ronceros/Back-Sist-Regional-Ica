package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.response.MedicoResponsePublicDTO;
import com.clinicaregional.clinica.entity.TipoDocumento;
import org.springframework.stereotype.Component;

import com.clinicaregional.clinica.dto.request.MedicoRequestDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponseDTO;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Usuario;

@Component
public class MedicoMapper {

    public MedicoResponseDTO mapToMedicoResponseDTO(Medico medico) {
        return new MedicoResponseDTO(medico.getId(),
                medico.getNombres(), medico.getApellidos(),
                medico.getNumeroColegiatura(), medico.getNumeroRNE(),
                medico.getTipoDocumento().getId(),
                medico.getNumeroDocumento(),
                medico.getTelefono(), medico.getDireccion(),
                medico.getDescripcion(), medico.getImagen(),
                medico.getFechaContratacion(), medico.getTipoContrato(),
                medico.getTipoMedico(), medico.getUsuario().getId());
    }

    //para mapear hacia MedicoResponsePublicDTO

    public MedicoResponsePublicDTO mapToMedicoResponsePublicDTO(Medico medico) {
        return new MedicoResponsePublicDTO(
                medico.getId(),
                medico.getNombres(),
                medico.getApellidos(),
                medico.getNumeroColegiatura(),
                medico.getNumeroRNE(),
                medico.getDescripcion(),
                medico.getImagen()
        );
    }

    public Medico mapToMedico(MedicoRequestDTO dto, Usuario usuario) {
        TipoDocumento tipoDocumento= new TipoDocumento();
        tipoDocumento.setId(dto.getTipoDocumentoId());
        Medico medico = new Medico();
        medico.setNombres(dto.getNombres());
        medico.setApellidos(dto.getApellidos());
        medico.setNumeroColegiatura(dto.getNumeroColegiatura());
        medico.setNumeroRNE(dto.getNumeroRNE());
        medico.setTipoDocumento(tipoDocumento);
        medico.setNumeroDocumento(dto.getNumeroDocumento());
        medico.setTelefono(dto.getTelefono());
        medico.setDireccion(dto.getDireccion());
        medico.setDescripcion(dto.getDescripcion());
        medico.setImagen(dto.getImagen());
        medico.setFechaContratacion(dto.getFechaContratacion());
        medico.setTipoContrato(dto.getTipoContrato());
        medico.setTipoMedico(dto.getTipoMedico());
        medico.setUsuario(usuario);
        return medico;
    }

}
