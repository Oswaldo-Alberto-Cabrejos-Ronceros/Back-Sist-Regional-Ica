package com.clinicaregional.clinica.mapper;

import org.springframework.stereotype.Component;

import com.clinicaregional.clinica.dto.MedicoRequestDTO;
import com.clinicaregional.clinica.dto.MedicoResponseDTO;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Usuario;

@Component
public class MedicoMapper {
    
    public MedicoResponseDTO mapToMedicoResponseDTO(Medico medico) {
        return new MedicoResponseDTO(
            medico.getId(),
            medico.getNombres(),
            medico.getApellidos(),
            medico.getNumeroColegiatura(),
            medico.getNumeroRNE(),
            medico.getTelefono(),
            medico.getDireccion(),
            medico.getDescripcion(),
            medico.getImagen(),
            medico.getFechaContratacion(),
            medico.getTipoContrato(),
            medico.getTipoMedico()
        );
    }

    public Medico mapToMedico(MedicoRequestDTO dto, Usuario usuario) {
        Medico medico = new Medico();
        medico.setNombres(dto.getNombres());
        medico.setApellidos(dto.getApellidos());
        medico.setNumeroColegiatura(dto.getNumeroColegiatura());
        medico.setNumeroRNE(dto.getNumeroRNE());
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
