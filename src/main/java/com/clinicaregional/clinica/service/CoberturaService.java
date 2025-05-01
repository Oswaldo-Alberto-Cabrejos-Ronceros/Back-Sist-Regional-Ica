package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.CoberturaDTO;

import java.util.List;
import java.util.Optional;

public interface CoberturaService {
    List<CoberturaDTO> listarCoberturas();

    Optional<CoberturaDTO> getCoberturaById(Long id);

    CoberturaDTO createCobertura(CoberturaDTO coberturaDTO);

    CoberturaDTO updateCobertura(Long id, CoberturaDTO coberturaDTO);

    void deleteCobertura(Long id);
}
