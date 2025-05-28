package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.SeguroCoberturaDTO;
import com.clinicaregional.clinica.entity.Cobertura;
import com.clinicaregional.clinica.entity.Seguro;
import com.clinicaregional.clinica.entity.SeguroCobertura;
import org.springframework.stereotype.Component;

@Component
public class SeguroCoberturaMapper {
    public SeguroCoberturaDTO mapToSeguroCoberturaDTO(SeguroCobertura seguroCobertura) {
        return new SeguroCoberturaDTO(seguroCobertura.getId(), seguroCobertura.getSeguro().getId(), seguroCobertura.getCobertura().getId());
    }

    public SeguroCobertura mapToSeguroCobertura(SeguroCoberturaDTO seguroCoberturaDTO) {
        Seguro seguro = new Seguro();
        seguro.setId(seguroCoberturaDTO.getSeguroId());
        Cobertura cobertura = new Cobertura();
        cobertura.setId(seguroCoberturaDTO.getCoberturaId());
        return new SeguroCobertura(seguroCoberturaDTO.getId(), seguro, cobertura);
    }
}
