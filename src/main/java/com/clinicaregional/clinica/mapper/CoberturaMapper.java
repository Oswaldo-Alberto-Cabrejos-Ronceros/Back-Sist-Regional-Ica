package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.CoberturaDTO;
import com.clinicaregional.clinica.entity.Cobertura;
import org.springframework.stereotype.Component;

@Component
public class CoberturaMapper {
    public CoberturaDTO mapToCoberturaDTO(Cobertura cobertura) {
        return new CoberturaDTO(
                cobertura.getId(),
                cobertura.getNombre(),
                cobertura.getDescripcion()
        );
    }

    public Cobertura mapToCobertura(CoberturaDTO coberturaDTO) {
        return new Cobertura(
                coberturaDTO.getId(),
                coberturaDTO.getNombre(),
                coberturaDTO.getDescripcion()
        );
    }
}
