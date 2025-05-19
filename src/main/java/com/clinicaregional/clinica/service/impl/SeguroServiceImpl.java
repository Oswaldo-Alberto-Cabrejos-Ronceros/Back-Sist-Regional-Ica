package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.SeguroDTO;
import com.clinicaregional.clinica.entity.Seguro;
import com.clinicaregional.clinica.enums.EstadoSeguro;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.SeguroMapper;
import com.clinicaregional.clinica.repository.SeguroRepository;
import com.clinicaregional.clinica.service.SeguroService;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeguroServiceImpl implements SeguroService {
    private final SeguroRepository seguroRepository;
    private final SeguroMapper seguroMapper;
    private final FiltroEstado filtroEstado;

    @Autowired
    public SeguroServiceImpl(SeguroRepository seguroRepository, SeguroMapper seguroMapper,FiltroEstado filtroEstado) {
        this.seguroRepository = seguroRepository;
        this.seguroMapper = seguroMapper;
        this.filtroEstado = filtroEstado;
    }

    @Transactional(readOnly = true)
    @Override
    public List<SeguroDTO> listarSeguros() {
        filtroEstado.activarFiltroEstado(true);
        return seguroRepository.findAll().stream().map(seguroMapper::mapToSeguroDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<SeguroDTO> getSeguroById(Long id) {
        return seguroRepository.findByIdAndEstadoIsTrue(id).map(seguroMapper::mapToSeguroDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<SeguroDTO> getSeguroByNombre(String nombre) {
        filtroEstado.activarFiltroEstado(true);
        return seguroRepository.findByNombre(nombre).map(seguroMapper::mapToSeguroDTO);
    }

    @Transactional
    @Override
    public SeguroDTO createSeguro(SeguroDTO seguroDTO) {
        filtroEstado.activarFiltroEstado(true);;
        if (seguroRepository.existsByNombre(seguroDTO.getNombre())) {
            throw new RuntimeException("El nombre ya existe");
        }
        Seguro seguro = seguroMapper.mapToSeguro(seguroDTO);

        Seguro savedSeguro = seguroRepository.save(seguro);
        return seguroMapper.mapToSeguroDTO(savedSeguro);
    }

    @Transactional
    @Override
    public SeguroDTO updateSeguro(Long id, SeguroDTO seguroDTO) {
        filtroEstado.activarFiltroEstado(true);

        Seguro findSeguro = seguroRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el seguro con el id: " + id));

        boolean nombreDuplicado = seguroRepository.existsByNombreAndEstadoIsTrue(seguroDTO.getNombre()) &&
                !findSeguro.getNombre().equalsIgnoreCase(seguroDTO.getNombre());

        if (nombreDuplicado) {
            throw new DuplicateResourceException("Ya existe un seguro con el nombre ingresado");
        }

        findSeguro.setNombre(seguroDTO.getNombre());
        findSeguro.setDescripcion(seguroDTO.getDescripcion());
        findSeguro.setImagenUrl(seguroDTO.getImagenUrl());
        findSeguro.setEstadoSeguro(seguroDTO.getEstadoSeguro());

        Seguro updatedSeguro = seguroRepository.save(findSeguro);
        return seguroMapper.mapToSeguroDTO(updatedSeguro);
    }


    @Transactional
    @Override
    public SeguroDTO updateEstadoSeguro(Long id, EstadoSeguro estadoSeguro) {
        filtroEstado.activarFiltroEstado(true);
        Seguro findSeguro = seguroRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new RuntimeException("No se encontro el seguro con el id: " + id));
        if (estadoSeguro.equals(findSeguro.getEstadoSeguro())) {
            throw new RuntimeException("El estado seguro es el mismo");
        }
        findSeguro.setEstadoSeguro(estadoSeguro);
        Seguro updatedSeguro = seguroRepository.save(findSeguro);
        return seguroMapper.mapToSeguroDTO(updatedSeguro);
    }

    @Transactional
    @Override
    public void deleteSeguro(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Seguro findSeguro = seguroRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new RuntimeException("No se encontro el seguro con el id: " + id));
        findSeguro.setEstado(false);
        seguroRepository.save(findSeguro);
    }

}
