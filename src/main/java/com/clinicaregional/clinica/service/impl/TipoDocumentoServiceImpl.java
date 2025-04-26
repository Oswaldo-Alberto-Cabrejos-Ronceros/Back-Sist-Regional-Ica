package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.TipoDocumentoDTO;
import com.clinicaregional.clinica.entity.TipoDocumento;
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
public class TipoDocumentoServiceImpl extends FiltroEstado implements TipoDocumentoService {

    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final TipoDocumentoMapper tipoDocumentoMapper;

    @Autowired
    public TipoDocumentoServiceImpl(TipoDocumentoRepository tipoDocumentoRepository, TipoDocumentoMapper tipoDocumentoMapper) {
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.tipoDocumentoMapper = tipoDocumentoMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public List<TipoDocumentoDTO> listarTipoDocumento() {
        activarFiltroEstado(true);
        return tipoDocumentoRepository.findAll().stream().map(tipoDocumentoMapper::mapToTipoDocumentoDTO).collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    @Override
    public Optional<TipoDocumentoDTO> getTipoDocumentoById(Long id) {
        activarFiltroEstado(true);
        return tipoDocumentoRepository.findByIdAndEstadoIsTrue(id).map(tipoDocumentoMapper::mapToTipoDocumentoDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<TipoDocumento> getTipoDocumentoByIdContext(Long id) {
        activarFiltroEstado(true);
        return tipoDocumentoRepository.findByIdAndEstadoIsTrue(id);
    }

    @Transactional
    @Override
    public TipoDocumentoDTO createTipoDocumento(TipoDocumentoDTO tipoDocumento) {
        activarFiltroEstado(true);
        if(tipoDocumentoRepository.existsByNombreAndEstadoIsTrue(tipoDocumento.getNombre())){
            throw new IllegalStateException("Tipo de documento ya existe en el sistema");
        }
        TipoDocumento tipoDocumentoMaped = tipoDocumentoMapper.mapToTipoDocumento(tipoDocumento);
        TipoDocumento savedTipoDocuemento = tipoDocumentoRepository.save(tipoDocumentoMaped);
        return tipoDocumentoMapper.mapToTipoDocumentoDTO(savedTipoDocuemento);
    }

    @Transactional
    @Override
    public TipoDocumentoDTO updateTipoDocumento(Long id, TipoDocumentoDTO tipoDocumento) {
        activarFiltroEstado(true);
        TipoDocumento tipoDocumentoExist = tipoDocumentoRepository.findByIdAndEstadoIsTrue(id).orElseThrow(()->new RuntimeException("Tipo de documento no encontrado"));
        if(tipoDocumentoRepository.existsByNombreAndEstadoIsTrue(tipoDocumento.getNombre())){
            throw new IllegalStateException("Tipo de documento ya existe en el sistema");
        }
        tipoDocumentoExist.setNombre(tipoDocumento.getNombre());
        tipoDocumentoExist.setDescripcion(tipoDocumento.getDescripcion());
        TipoDocumento savedTipoDocumento = tipoDocumentoRepository.save(tipoDocumentoExist);
        return tipoDocumentoMapper.mapToTipoDocumentoDTO(savedTipoDocumento);
    }

    @Override
    public void deleteTipoDocumento(Long id) {
        activarFiltroEstado(true);
        TipoDocumento tipoDocumentoExist = tipoDocumentoRepository.findByIdAndEstadoIsTrue(id).orElseThrow(()->new RuntimeException("Tipo de documento no encontrado"));
        tipoDocumentoExist.setEstado(false); // borrado logico
        tipoDocumentoRepository.save(tipoDocumentoExist);
    }
}
