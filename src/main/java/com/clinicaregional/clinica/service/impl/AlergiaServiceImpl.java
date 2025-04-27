package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.AlergiaDTO;
import com.clinicaregional.clinica.entity.Alergia;
import com.clinicaregional.clinica.enums.TipoAlergia;
import com.clinicaregional.clinica.mapper.AlergiaMapper;
import com.clinicaregional.clinica.repository.AlergiaRepository;
import com.clinicaregional.clinica.service.AlergiaService;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlergiaServiceImpl implements AlergiaService {

    private final AlergiaRepository alergiaRepository;
    private final AlergiaMapper alergiaMapper;
    private final FiltroEstado filtroEstado;

    @Autowired
    public AlergiaServiceImpl(AlergiaRepository alergiaRepository,
            AlergiaMapper alergiaMapper,
            FiltroEstado filtroEstado) {
        this.alergiaRepository = alergiaRepository;
        this.alergiaMapper = alergiaMapper;
        this.filtroEstado = filtroEstado;
    }

    @Transactional(readOnly = true)
    @Override
    public List<AlergiaDTO> listarAlergias() {
        filtroEstado.activarFiltroEstado(true);
        return alergiaRepository.findAll().stream()
                .map(alergiaMapper::mapToAlergiaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<AlergiaDTO> listarAlergiasPorTipo(TipoAlergia tipoAlergia) {
        filtroEstado.activarFiltroEstado(true);
        return alergiaRepository.findByTipoAlergia(tipoAlergia).stream()
                .map(alergiaMapper::mapToAlergiaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AlergiaDTO> getAlergiaPorId(Long id) {
        filtroEstado.activarFiltroEstado(true);
        return alergiaRepository.findByIdAndEstadoIsTrue(id)
                .map(alergiaMapper::mapToAlergiaDTO);
    }

    @Transactional
    @Override
    public AlergiaDTO crearAlergia(AlergiaDTO alergiaDTO) {
        filtroEstado.activarFiltroEstado(true);
        if (alergiaRepository.existsByNombreAndEstadoIsTrue(alergiaDTO.getNombre())) {
            throw new RuntimeException("El nombre ya existe");
        }
        Alergia alergia = alergiaMapper.mapToAlergia(alergiaDTO);
        Alergia alergiaSaved = alergiaRepository.save(alergia);
        return alergiaMapper.mapToAlergiaDTO(alergiaSaved);
    }

    @Transactional
    @Override
    public AlergiaDTO updateAlergia(Long id, AlergiaDTO alergiaDTO) {
        filtroEstado.activarFiltroEstado(true);
        Alergia alergia = alergiaRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el id"));
        if (alergiaRepository.existsByNombreAndEstadoIsTrue(alergiaDTO.getNombre())) {
            throw new RuntimeException("El nombre ya existe");
        }
        alergia.setNombre(alergiaDTO.getNombre());
        alergia.setTipoAlergia(alergiaDTO.getTipoAlergia());
        Alergia updatedAlergia = alergiaRepository.save(alergia);
        return alergiaMapper.mapToAlergiaDTO(updatedAlergia);
    }

    @Transactional
    @Override
    public void eliminarAlergia(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Alergia alergia = alergiaRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el id"));
        alergia.setEstado(false); // borrado lógico
        alergiaRepository.save(alergia);
    }
}
