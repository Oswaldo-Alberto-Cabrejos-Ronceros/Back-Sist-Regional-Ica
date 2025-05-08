package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.ServicioSeguroDTO;
import com.clinicaregional.clinica.entity.ServicioSeguro;
import com.clinicaregional.clinica.mapper.ServicioSeguroMapper;
import com.clinicaregional.clinica.repository.ServicioRepository;
import com.clinicaregional.clinica.repository.ServicioSeguroRepository;
import com.clinicaregional.clinica.service.CoberturaService;
import com.clinicaregional.clinica.service.SeguroCoberturaService;
import com.clinicaregional.clinica.service.SeguroService;
import com.clinicaregional.clinica.service.ServicioSeguroService;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicioSeguroServiceImpl implements ServicioSeguroService {

    private final ServicioSeguroRepository servicioSeguroRepository;
    private final ServicioSeguroMapper servicioSeguroMapper;
    private final FiltroEstado filtroEstado;
    private final ServicioRepository servicioRepository;
    private final SeguroService seguroService;
    private final CoberturaService coberturaService;
    private final SeguroCoberturaService seguroCoberturaService;

    @Autowired
    public ServicioSeguroServiceImpl(ServicioSeguroRepository servicioSeguroRepository, ServicioSeguroMapper servicioSeguroMapper, FiltroEstado filtroEstado,
                                     ServicioRepository servicioRepository, SeguroService seguroService, CoberturaService coberturaService, SeguroCoberturaService seguroCoberturaService) {
        this.servicioSeguroRepository = servicioSeguroRepository;
        this.servicioSeguroMapper = servicioSeguroMapper;
        this.filtroEstado = filtroEstado;
        this.servicioRepository = servicioRepository;
        this.seguroService = seguroService;
        this.coberturaService = coberturaService;
        this.seguroCoberturaService = seguroCoberturaService;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ServicioSeguroDTO> listarServicioSeguro() {
        filtroEstado.activarFiltroEstado(true);
        return servicioSeguroRepository.findAll().stream().map(servicioSeguroMapper::mapToServicioSeguroDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ServicioSeguroDTO> listarPorServicio(Long servicioId) {
        filtroEstado.activarFiltroEstado(true);
        return servicioSeguroRepository.findByServicio_Id(servicioId).stream().map(servicioSeguroMapper::mapToServicioSeguroDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ServicioSeguroDTO> listarPorSeguro(Long seguroId) {
        filtroEstado.activarFiltroEstado(true);
        return servicioSeguroRepository.findBySeguro_Id(seguroId).stream().map(servicioSeguroMapper::mapToServicioSeguroDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ServicioSeguroDTO> listarPorCobertura(Long coberturaId) {
        filtroEstado.activarFiltroEstado(true);
        return servicioSeguroRepository.findByCobertura_Id(coberturaId).stream().map(servicioSeguroMapper::mapToServicioSeguroDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ServicioSeguroDTO> getSeguroServicioById(Long id) {
        return servicioSeguroRepository.findByIdAndEstadoIsTrue(id).map(servicioSeguroMapper::mapToServicioSeguroDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ServicioSeguroDTO> getSeguroServicioBySeguroAndServicio(Long seguroId, Long servicioId) {
        return servicioSeguroRepository.findBySeguro_IdAndServicio_Id(seguroId,servicioId).map(servicioSeguroMapper::mapToServicioSeguroDTO);
    }

    @Transactional
    @Override
    public ServicioSeguroDTO createServicioSeguro(ServicioSeguroDTO servicioSeguroDTO) {
        filtroEstado.activarFiltroEstado(true);
        servicioRepository.findByIdAndEstadoIsTrue(servicioSeguroDTO.getServicioId()).orElseThrow(() -> new RuntimeException("No se encontro el servicio con el id ingresado"));
        seguroService.getSeguroById(servicioSeguroDTO.getSeguroId());
        coberturaService.getCoberturaById(servicioSeguroDTO.getCoberturaId());
        if (!seguroCoberturaService.existsBySeguroAndCobertura(servicioSeguroDTO.getServicioId(), servicioSeguroDTO.getCoberturaId())) {
            throw new RuntimeException("El seguro no cubre la cobertura ingresada");
        }

        if (servicioSeguroRepository.existsByServicio_IdAndSeguro_IdAndCobertura_Id(servicioSeguroDTO.getServicioId(), servicioSeguroDTO.getSeguroId(),
                servicioSeguroDTO.getCoberturaId())) {
            throw new RuntimeException("Ya existe un ServicioSeguro con el servicio, seguro y cobertura ingresado");
        }
        ServicioSeguro savedServicioSeguro = servicioSeguroRepository.save(servicioSeguroMapper.mapToServicioSeguro(servicioSeguroDTO));
        return servicioSeguroMapper.mapToServicioSeguroDTO(savedServicioSeguro);
    }

    @Transactional
    @Override
    public void deleteServicioSeguro(Long id) {
        ServicioSeguro findServicioSeguro = servicioSeguroRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new RuntimeException("No se encontro el Servicio Seguro con el id ingresado"));
        findServicioSeguro.setEstado(false);
        servicioSeguroRepository.save(findServicioSeguro);
    }
}
