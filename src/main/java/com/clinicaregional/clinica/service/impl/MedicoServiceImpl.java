package com.clinicaregional.clinica.service.impl;

import java.util.stream.Collectors;
import java.util.List;

import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinicaregional.clinica.repository.MedicoRepository;
import com.clinicaregional.clinica.dto.request.MedicoRequestDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponseDTO;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.mapper.MedicoMapper;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.service.MedicoService;

@Service
public class MedicoServiceImpl extends FiltroEstado implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public MedicoServiceImpl(MedicoRepository medicoRepository, MedicoMapper medicoMapper, UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.medicoRepository = medicoRepository;
        this.medicoMapper = medicoMapper;
    }

    @Override
    public List<MedicoResponseDTO> obtenerMedicos() {
        activarFiltroEstado(true);
        return medicoRepository.findAll()
                .stream()
                .map(medicoMapper::mapToMedicoResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MedicoResponseDTO guardarMedico(MedicoRequestDTO dto) {
        activarFiltroEstado(true);
        Usuario usuario = usuarioRepository.findByIdAndEstadoIsTrue(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getUsuarioId()));
        Medico medico = medicoMapper.mapToMedico(dto, usuario);
        Medico guardado = medicoRepository.save(medico);
        return medicoMapper.mapToMedicoResponseDTO(guardado);
    }

    @Override
    public MedicoResponseDTO actualizarMedico(Long id, MedicoRequestDTO dto) {
        activarFiltroEstado(true);
        Medico medico = medicoRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con ID: " + id));

        Usuario usuario = usuarioRepository.findByIdAndEstadoIsTrue(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getUsuarioId()));

        // Actualizar campos
        medico.setNombres(dto.getNombres());
        medico.setApellidos(dto.getApellidos());
        medico.setNumeroColegiatura(dto.getNumeroColegiatura());
        medico.setNumeroRNE(dto.getNumeroRNE());
        medico.setTelefono(dto.getTelefono());
        medico.setDireccion(dto.getDireccion());
        medico.setDescripcion(dto.getDescripcion());
        medico.setImagen(dto.getImagen());
        medico.setFechaContratacion(dto.getFechaContratacion());
        medico.setTipoContrato(dto.getTipoContrato());
        medico.setTipoMedico(dto.getTipoMedico());
        medico.setUsuario(usuario);

        Medico actualizado = medicoRepository.save(medico);
        return medicoMapper.mapToMedicoResponseDTO(actualizado);
    }

    @Override
    public void eliminarMedico(Long id) {
        activarFiltroEstado(true);
        Medico medico = medicoRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new RuntimeException("Medico no encontrado con ID: " + id));
        medico.setEstado(false); //borrado logico
        medicoRepository.save(medico);
    }
}
