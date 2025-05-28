package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.SeguroDTO;
import com.clinicaregional.clinica.entity.Seguro;
import org.springframework.stereotype.Component;

@Component
public class SeguroMapper {
    public SeguroDTO mapToSeguroDTO(Seguro seguro) {
        return new SeguroDTO(
                seguro.getId(),
                seguro.getNombre(),
                seguro.getDescripcion(),
                seguro.getImagenUrl(),
                seguro.getEstadoSeguro()
        );
    }

    public Seguro mapToSeguro(SeguroDTO seguroDTO) {
        return new Seguro(
                seguroDTO.getId(),
                seguroDTO.getNombre(),
                seguroDTO.getDescripcion(),
                seguroDTO.getImagenUrl(),
                seguroDTO.getEstadoSeguro()
        );
    }
}