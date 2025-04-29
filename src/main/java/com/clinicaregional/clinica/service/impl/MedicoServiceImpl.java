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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    @Override
    public List<MedicoResponseDTO> obtenerMedicos() {
        activarFiltroEstado(true);
        return medicoRepository.findAll()
                .stream()
                .map(medicoMapper::mapToMedicoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public MedicoResponseDTO guardarMedico(MedicoRequestDTO dto) {
        activarFiltroEstado(true);
        if(medicoRepository.existsByNumeroColegiatura(dto.getNumeroColegiatura())) {
            throw new RuntimeException("Ya existe un medico con el numero de colegiatura ingresado");
        }
        if(medicoRepository.existsByNumeroRNE(dto.getNumeroRNE())) {
            throw new RuntimeException("Ya existe un medico con el rne ingresado");
        }
        Usuario usuario = usuarioRepository.findByIdAndEstadoIsTrue(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getUsuarioId()));
        if(medicoRepository.existsByUsuario(usuario)){
            throw new RuntimeException("Ya existe un medico con el usuario ingresado");
        }
        Medico medico = medicoMapper.mapToMedico(dto, usuario);
        Medico guardado = medicoRepository.save(medico);
        return medicoMapper.mapToMedicoResponseDTO(guardado);
    }

    @Transactional
    @Override
    public MedicoResponseDTO actualizarMedico(Long id, MedicoRequestDTO dto) {
        activarFiltroEstado(true);
        Medico medico = medicoRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con ID: " + id));

        Usuario usuario = usuarioRepository.findByIdAndEstadoIsTrue(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getUsuarioId()));

        if(medicoRepository.existsByNumeroColegiatura(dto.getNumeroColegiatura())) {
            throw new RuntimeException("Ya existe un medico con el numero de colegiatura ingresado");
        }
        if(medicoRepository.existsByNumeroRNE(dto.getNumeroRNE())) {
            throw new RuntimeException("Ya existe un medico con el rne ingresado");
        }
        if(medicoRepository.existsByUsuario(usuario)){
            throw new RuntimeException("Ya existe un medico con el usuario ingresado");
        }

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

    @Transactional
    @Override
    public void eliminarMedico(Long id) {
        activarFiltroEstado(true);
        Medico medico = medicoRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new RuntimeException("Medico no encontrado con ID: " + id));
        medico.setEstado(false); //borrado logico
        Usuario usuario = usuarioRepository.findByIdAndEstadoIsTrue(medico.getUsuario().getId()).orElseThrow(()->new RuntimeException("Usuario no encontrado"));
        usuario.setEstado(false);
        medico.setUsuario(null);
        usuarioRepository.save(usuario);
        medicoRepository.save(medico);
    }
}
