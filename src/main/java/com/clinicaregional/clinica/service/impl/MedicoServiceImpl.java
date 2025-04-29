package com.clinicaregional.clinica.service.impl;

import java.util.stream.Collectors;
import java.util.List;

import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinicaregional.clinica.repository.MedicoRepository;
import com.clinicaregional.clinica.dto.request.MedicoRequestDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.UsuarioRequestDTO;
import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponseDTO;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.mapper.MedicoMapper;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.service.MedicoService;
import com.clinicaregional.clinica.service.UsuarioService;

import org.springframework.transaction.annotation.Transactional;


@Service
public class MedicoServiceImpl extends FiltroEstado implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    @Autowired
    public MedicoServiceImpl(MedicoRepository medicoRepository, MedicoMapper medicoMapper, UsuarioRepository usuarioRepository, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.medicoRepository = medicoRepository;
        this.medicoMapper = medicoMapper;
        this.usuarioService = usuarioService;
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
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new RuntimeException("Ya existe un usuario con el correo ingresado");
        }

        UsuarioRequestDTO newUsuario = new UsuarioRequestDTO();
        newUsuario.setCorreo(dto.getCorreo());
        newUsuario.setPassword(dto.getPassword());
        RolDTO rolMedico = new RolDTO();
        rolMedico.setId(4L); // ID del rol de médico
        newUsuario.setRol(rolMedico);
        UsuarioDTO usuarioDTO = usuarioService.guardar(newUsuario);  
        Usuario usuario1 = new Usuario();
        usuario1.setId(usuarioDTO.getId());

        Medico medico = Medico.builder()
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .numeroColegiatura(dto.getNumeroColegiatura())
                .numeroRNE(dto.getNumeroRNE())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .descripcion(dto.getDescripcion())
                .imagen(dto.getImagen())
                .fechaContratacion(dto.getFechaContratacion())
                .tipoContrato(dto.getTipoContrato())
                .tipoMedico(dto.getTipoMedico())
                .usuario(usuario1)
                .build();

        return medicoMapper.mapToMedicoResponseDTO(medicoRepository.save(medico));

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
