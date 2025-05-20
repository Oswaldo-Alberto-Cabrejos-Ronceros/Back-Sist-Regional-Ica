package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.CoberturaDTO;
import com.clinicaregional.clinica.entity.Cobertura;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.CoberturaMapper;
import com.clinicaregional.clinica.repository.CoberturaRepository;
import com.clinicaregional.clinica.service.CoberturaService;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CoberturaServiceImpl  implements CoberturaService {

    private final CoberturaRepository coberturaRepository;
    private final CoberturaMapper coberturaMapper;
    private final FiltroEstado filtroEstado;

    @Autowired
    public CoberturaServiceImpl(CoberturaRepository coberturaRepository, CoberturaMapper coberturaMapper, FiltroEstado filtroEstado) {
        this.coberturaRepository = coberturaRepository;
        this.coberturaMapper = coberturaMapper;
        this.filtroEstado = filtroEstado;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CoberturaDTO> listarCoberturas() {
        filtroEstado.activarFiltroEstado(true);
        return coberturaRepository.findAll().stream().map(coberturaMapper::mapToCoberturaDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<CoberturaDTO> getCoberturaById(Long id) {
        return coberturaRepository.findByIdAndEstadoIsTrue(id).map(coberturaMapper::mapToCoberturaDTO);
    }

    @Transactional
    @Override
    public CoberturaDTO createCobertura(CoberturaDTO coberturaDTO) {
        filtroEstado.activarFiltroEstado(true);
        if (coberturaRepository.existsByNombre(coberturaDTO.getNombre())) {
            throw new DuplicateResourceException("El nombre del cobertura ya existe");
        }
        Cobertura savedCobertura = coberturaRepository.save(coberturaMapper.mapToCobertura(coberturaDTO));
        return coberturaMapper.mapToCoberturaDTO(savedCobertura);
    }

    @Transactional
    @Override
    public CoberturaDTO updateCobertura(Long id, CoberturaDTO coberturaDTO) {
        filtroEstado.activarFiltroEstado(true);
        Cobertura findCobertura = coberturaRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new ResourceNotFoundException("No existe una cobertura con el id: " + id));
        if (coberturaRepository.existsByNombre(coberturaDTO.getNombre())) {
            throw new DuplicateResourceException("El nombre del cobertura ya existe");
        }
        findCobertura.setNombre(coberturaDTO.getNombre());
        findCobertura.setDescripcion(coberturaDTO.getDescripcion());
        Cobertura updatedCobertura = coberturaRepository.save(findCobertura);
        return coberturaMapper.mapToCoberturaDTO(updatedCobertura);
    }

    @Transactional
    @Override
    public void deleteCobertura(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Cobertura findCobertura = coberturaRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new ResourceNotFoundException("No existe una cobertura con el id: " + id));
        findCobertura.setEstado(false);
        coberturaRepository.save(findCobertura);
    }
}
