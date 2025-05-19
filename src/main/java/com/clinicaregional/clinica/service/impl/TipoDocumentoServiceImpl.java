package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.TipoDocumentoDTO;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.TipoDocumentoMapper;
import com.clinicaregional.clinica.repository.TipoDocumentoRepository;
import com.clinicaregional.clinica.service.TipoDocumentoService;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TipoDocumentoServiceImpl implements TipoDocumentoService {

    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final TipoDocumentoMapper tipoDocumentoMapper;
    private final FiltroEstado filtroEstado;

    @Autowired
    public TipoDocumentoServiceImpl(TipoDocumentoRepository tipoDocumentoRepository, TipoDocumentoMapper tipoDocumentoMapper, FiltroEstado filtroEstado) {
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.tipoDocumentoMapper = tipoDocumentoMapper;
        this.filtroEstado = filtroEstado;
    }

    @Transactional(readOnly = true)
    @Override
    public List<TipoDocumentoDTO> listarTipoDocumento() {
        filtroEstado.activarFiltroEstado(true);
        return tipoDocumentoRepository.findAll()
                .stream()
                .map(tipoDocumentoMapper::mapToTipoDocumentoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<TipoDocumentoDTO> getTipoDocumentoById(Long id) {
        filtroEstado.activarFiltroEstado(true);
        return tipoDocumentoRepository.findByIdAndEstadoIsTrue(id)
                .map(tipoDocumentoMapper::mapToTipoDocumentoDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<TipoDocumento> getTipoDocumentoByIdContext(Long id) {
        filtroEstado.activarFiltroEstado(true);
        return tipoDocumentoRepository.findByIdAndEstadoIsTrue(id);
    }

    @Transactional
    @Override
    public TipoDocumentoDTO createTipoDocumento(TipoDocumentoDTO tipoDocumento) {
        filtroEstado.activarFiltroEstado(true);
        if (tipoDocumentoRepository.existsByNombreAndEstadoIsTrue(tipoDocumento.getNombre())) {
            throw new IllegalArgumentException("El tipo de documento ya existe en el sistema");
        }
        TipoDocumento tipoDocumentoMapped = tipoDocumentoMapper.mapToTipoDocumento(tipoDocumento);
        TipoDocumento savedTipoDocumento = tipoDocumentoRepository.save(tipoDocumentoMapped);
        return tipoDocumentoMapper.mapToTipoDocumentoDTO(savedTipoDocumento);
    }

    @Transactional
    @Override
    public TipoDocumentoDTO updateTipoDocumento(Long id, TipoDocumentoDTO tipoDocumento) {
        filtroEstado.activarFiltroEstado(true);

        TipoDocumento tipoDocumentoExist = tipoDocumentoRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un tipo de documento con el id: " + id));

        // Validar duplicado solo si el nombre nuevo es diferente al actual
        boolean nombreDuplicado = tipoDocumentoRepository.existsByNombreAndEstadoIsTrue(tipoDocumento.getNombre()) &&
                !tipoDocumentoExist.getNombre().equalsIgnoreCase(tipoDocumento.getNombre());

        if (nombreDuplicado) {
            throw new DuplicateResourceException("Ya existe un tipo de documento con el nombre ingresado");
        }

        tipoDocumentoExist.setNombre(tipoDocumento.getNombre());
        tipoDocumentoExist.setDescripcion(tipoDocumento.getDescripcion());

        TipoDocumento savedTipoDocumento = tipoDocumentoRepository.save(tipoDocumentoExist);
        return tipoDocumentoMapper.mapToTipoDocumentoDTO(savedTipoDocumento);
    }


    @Transactional
    @Override
    public void deleteTipoDocumento(Long id) {
        filtroEstado.activarFiltroEstado(true);
        TipoDocumento tipoDocumentoExist = tipoDocumentoRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un tipo de documento con el id"));
        tipoDocumentoExist.setEstado(false); // Borrado lógico
        tipoDocumentoRepository.save(tipoDocumentoExist);
    }
}
