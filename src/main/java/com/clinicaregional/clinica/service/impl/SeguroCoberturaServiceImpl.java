package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.CoberturaDTO;
import com.clinicaregional.clinica.dto.SeguroCoberturaDTO;
import com.clinicaregional.clinica.dto.SeguroDTO;
import com.clinicaregional.clinica.entity.SeguroCobertura;
import com.clinicaregional.clinica.mapper.SeguroCoberturaMapper;
import com.clinicaregional.clinica.repository.SeguroCoberturaRepository;
import com.clinicaregional.clinica.service.CoberturaService;
import com.clinicaregional.clinica.service.SeguroCoberturaService;
import com.clinicaregional.clinica.service.SeguroService;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeguroCoberturaServiceImpl implements SeguroCoberturaService {

    private final SeguroCoberturaRepository seguroCoberturaRepository;
    private final SeguroCoberturaMapper seguroCoberturaMapper;
    private final SeguroService seguroService;
    private final CoberturaService coberturaService;
    private final FiltroEstado filtroEstado;

    @Autowired
    public SeguroCoberturaServiceImpl(SeguroCoberturaRepository seguroCoberturaRepository, SeguroCoberturaMapper seguroCoberturaMapper, SeguroService seguroService, CoberturaService coberturaService,FiltroEstado filtroEstado) {
        this.seguroCoberturaRepository = seguroCoberturaRepository;
        this.seguroCoberturaMapper = seguroCoberturaMapper;
        this.seguroService = seguroService;
        this.coberturaService = coberturaService;
        this.filtroEstado = filtroEstado;
    }

    @Transactional(readOnly = true)
    @Override
    public List<SeguroCoberturaDTO> listarSeguroCobertura() {
       filtroEstado.activarFiltroEstado(true);
        return seguroCoberturaRepository.findAll().stream().map(seguroCoberturaMapper::mapToSeguroCoberturaDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<SeguroCoberturaDTO> listarPorSeguro(Long seguroId) {
        filtroEstado.activarFiltroEstado(true);
        return seguroCoberturaRepository.findBySeguro_Id(seguroId).stream().map(seguroCoberturaMapper::mapToSeguroCoberturaDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<SeguroCoberturaDTO> listarPorCobertura(Long coberturaId) {
        filtroEstado.activarFiltroEstado(true);
        return seguroCoberturaRepository.findByCobertura_Id(coberturaId).stream().map(seguroCoberturaMapper::mapToSeguroCoberturaDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<SeguroCoberturaDTO> getSeguroCoberturaById(Long id) {
        return seguroCoberturaRepository.findByIdAndEstadoIsTrue(id).map(seguroCoberturaMapper::mapToSeguroCoberturaDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsBySeguroAndCobertura(Long seguroId, Long coberturaId) {
        filtroEstado.activarFiltroEstado(true);
        return seguroCoberturaRepository.existsBySeguro_IdAndCobertura_Id(seguroId, coberturaId);
    }

    @Transactional
    @Override
    public SeguroCoberturaDTO createSeguroCobertura(SeguroCoberturaDTO seguroCoberturaDTO) {
        filtroEstado.activarFiltroEstado(true);
        SeguroDTO seguroDTO = seguroService.getSeguroById(seguroCoberturaDTO.getSeguroId()).orElseThrow(() -> new RuntimeException("No existe un seguro con el id ingresado"));
        CoberturaDTO coberturaDTO = coberturaService.getCoberturaById(seguroCoberturaDTO.getCoberturaId()).orElseThrow(() -> new RuntimeException("No existe cobertura con el id ingresado"));
        if (seguroCoberturaRepository.existsBySeguro_IdAndCobertura_Id(seguroDTO.getId(),coberturaDTO.getId())) {
            throw new RuntimeException("Ya existe esta relación");
        }
        SeguroCobertura savedSeguroCobertura = seguroCoberturaRepository.save(seguroCoberturaMapper.mapToSeguroCobertura(seguroCoberturaDTO));
        return seguroCoberturaMapper.mapToSeguroCoberturaDTO(savedSeguroCobertura);
    }

    @Transactional
    @Override
    public void deleteSeguroCobertura(Long id) {
        filtroEstado.activarFiltroEstado(true);
        SeguroCobertura seguroCobertura = seguroCoberturaRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new RuntimeException("No existe SeguroCobertura con el id ingresado"));
        seguroCobertura.setEstado(false);
        seguroCoberturaRepository.save(seguroCobertura);
    }
}
