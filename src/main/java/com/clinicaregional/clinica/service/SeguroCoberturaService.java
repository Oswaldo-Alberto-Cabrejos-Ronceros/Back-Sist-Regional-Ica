package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.SeguroCoberturaDTO;

import java.util.List;
import java.util.Optional;

public interface SeguroCoberturaService {
    List<SeguroCoberturaDTO> listarSeguroCobertura();
    List<SeguroCoberturaDTO> listarPorSeguro(Long seguroId);
    List<SeguroCoberturaDTO> listarPorCobertura(Long coberturaId);
    Optional<SeguroCoberturaDTO> getSeguroCoberturaById(Long id);
    boolean existsBySeguroAndCobertura(Long seguroId, Long coberturaId);
    SeguroCoberturaDTO createSeguroCobertura(SeguroCoberturaDTO seguroCoberturaDTO);
    void deleteSeguroCobertura(Long id);
}
