package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.SeguroDTO;
import com.clinicaregional.clinica.entity.Seguro;
import com.clinicaregional.clinica.enums.EstadoSeguro;
import com.clinicaregional.clinica.exception.BadRequestException;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.SeguroMapper;
import com.clinicaregional.clinica.repository.SeguroRepository;
import com.clinicaregional.clinica.service.S3ServicePublic;
import com.clinicaregional.clinica.service.SeguroService;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeguroServiceImpl implements SeguroService {
    private final SeguroRepository seguroRepository;
    private final SeguroMapper seguroMapper;
    private final FiltroEstado filtroEstado;
    private final S3ServicePublic s3Service;

    @Autowired
    public SeguroServiceImpl(SeguroRepository seguroRepository, SeguroMapper seguroMapper, FiltroEstado filtroEstado, S3ServicePublic s3Service) {
        this.seguroRepository = seguroRepository;
        this.seguroMapper = seguroMapper;
        this.filtroEstado = filtroEstado;
        this.s3Service = s3Service;
    }

    @Transactional(readOnly = true)
    @Override
    public List<SeguroDTO> listarSeguros() {
        filtroEstado.activarFiltroEstado(true);
        List<SeguroDTO> seguros = seguroRepository.findAll().stream().map(seguroMapper::mapToSeguroDTO).toList();
        return seguros.stream().map(this::agregarUrlImage).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<SeguroDTO> getSeguroById(Long id) {
        Optional<SeguroDTO> seguroDTO = seguroRepository.findByIdAndEstadoIsTrue(id).map(seguroMapper::mapToSeguroDTO);
        return seguroDTO.map(this::agregarUrlImage);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<SeguroDTO> getSeguroByNombre(String nombre) {
        filtroEstado.activarFiltroEstado(true);
        Optional<SeguroDTO> seguro = seguroRepository.findByNombre(nombre).map(seguroMapper::mapToSeguroDTO);
        return seguro.map(this::agregarUrlImage);
    }

    @Transactional
    @Override
    public SeguroDTO createSeguro(SeguroDTO seguroDTO, MultipartFile imagen) {
        filtroEstado.activarFiltroEstado(true);

        if (seguroRepository.existsByNombre(seguroDTO.getNombre())) {
            throw new DuplicateResourceException("El nombre ya existe");
        }

        Seguro seguro = seguroMapper.mapToSeguro(seguroDTO);
        if (imagen != null) {
            String key = s3Service.subirArchivo(imagen, "seguro" + seguro.getNombre());
            seguro.setImagenUrl(key);
        }
        Seguro savedSeguro = seguroRepository.save(seguro);
        SeguroDTO response = seguroMapper.mapToSeguroDTO(savedSeguro);
        return this.agregarUrlImage(response);
    }

    @Transactional
    @Override
    public SeguroDTO updateSeguro(Long id, SeguroDTO seguroDTO, MultipartFile imagen) {
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

        if (imagen != null) {
            if (findSeguro.getImagenUrl() != null) {
                s3Service.eliminarArchivo(findSeguro.getImagenUrl());
            }
            String key = s3Service.subirArchivo(imagen, "servicios" + findSeguro.getNombre());
            findSeguro.setImagenUrl(key);
        }

        Seguro updatedSeguro = seguroRepository.save(findSeguro);
        SeguroDTO response = seguroMapper.mapToSeguroDTO(updatedSeguro);
        return this.agregarUrlImage(response);
    }

    @Transactional
    @Override
    public SeguroDTO updateEstadoSeguro(Long id, EstadoSeguro estadoSeguro) {
        filtroEstado.activarFiltroEstado(true);
        Seguro findSeguro = seguroRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro el seguro con el id: " + id));
        if (estadoSeguro.equals(findSeguro.getEstadoSeguro())) {
            throw new BadRequestException("El estado seguro es el mismo");
        }
        findSeguro.setEstadoSeguro(estadoSeguro);
        Seguro updatedSeguro = seguroRepository.save(findSeguro);
        SeguroDTO seguroDTO = seguroMapper.mapToSeguroDTO(updatedSeguro);
        return this.agregarUrlImage(seguroDTO);
    }

    @Transactional
    @Override
    public void deleteSeguro(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Seguro findSeguro = seguroRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro el seguro con el id: " + id));
        findSeguro.setEstado(false);
        if(findSeguro.getImagenUrl()!=null){
            s3Service.eliminarArchivo(findSeguro.getImagenUrl());
        }
        seguroRepository.save(findSeguro);
    }

    //funcion para obtener el url
    private SeguroDTO agregarUrlImage(SeguroDTO seguroDTO) {
        if (seguroDTO.getImagenUrl() != null) {
            String imageUrl = s3Service.generarUrlPublico(seguroDTO.getImagenUrl());
            seguroDTO.setImagenUrl(imageUrl);
        }
        return seguroDTO;
    }
}
