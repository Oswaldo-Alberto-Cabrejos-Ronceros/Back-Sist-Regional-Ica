package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.AdministradorDTO;
import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.RegisterAdministradorRequest;
import com.clinicaregional.clinica.entity.Administrador;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.mapper.AdministradorMapper;
import com.clinicaregional.clinica.repository.AdministradorRepository;
import com.clinicaregional.clinica.service.AdministradorService;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdministradorServiceImpl implements AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final AdministradorMapper administradorMapper;
    private final UsuarioService usuarioService;
    private final FiltroEstado filtroEstado;

    @Autowired
    public AdministradorServiceImpl(AdministradorRepository administradorRepository, AdministradorMapper administradorMapper, UsuarioService usuarioService, FiltroEstado filtroEstado) {
        this.administradorRepository = administradorRepository;
        this.administradorMapper = administradorMapper;
        this.usuarioService = usuarioService;
        this.filtroEstado = filtroEstado;
    }

    @Transactional(readOnly = true)
    @Override
    public List<AdministradorDTO> listarAdministradores() {
        filtroEstado.activarFiltroEstado(true);
        return administradorRepository.findAll().stream().map(administradorMapper::mapToAdministradorDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AdministradorDTO> getAdministradorById(Long id) {
        filtroEstado.activarFiltroEstado(true);
        return administradorRepository.findByIdAndEstadoIsTrue(id).map(administradorMapper::mapToAdministradorDTO);
    }

    @Transactional
    @Override
    public AdministradorDTO createAdministrador(RegisterAdministradorRequest registerAdministradorRequest) {
        filtroEstado.activarFiltroEstado(true);
        if (administradorRepository.existsByNumeroDocumento(registerAdministradorRequest.getAdministrador().getNumeroDocumento())) {
            throw new RuntimeException("Ya existe un administrador con el numero de documento ingresado");
        }

        // Establecer el rol por defecto (ADMIN)
        registerAdministradorRequest.getUsuario().setRol(new RolDTO(2L, "ADMINISTRADOR"));

        UsuarioDTO usuarioGuardado = usuarioService.guardar(registerAdministradorRequest.getUsuario());

        registerAdministradorRequest.getAdministrador().setUsuarioId(usuarioGuardado.getId());

        Administrador savedAdministrador = administradorRepository.save(administradorMapper.mapToAdministrador(registerAdministradorRequest.getAdministrador()));

        return administradorMapper.mapToAdministradorDTO(savedAdministrador);
    }

    @Transactional
    @Override
    public AdministradorDTO updateAdministrador(Long id, AdministradorDTO administradorDTO) {
        filtroEstado.activarFiltroEstado(true);
        Administrador findAdministrador = administradorRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new RuntimeException("No existe un administrador con el id ingresado"));

        if (administradorRepository.existsByNumeroDocumento(administradorDTO.getNumeroDocumento())) {
            throw new RuntimeException("Ya existe un administrador con el numero de documento ingresado");
        }
        if (administradorRepository.existsByUsuario_Id(administradorDTO.getUsuarioId())) {
            throw new RuntimeException("Ya existe un administrador con el usuario ingresado");
        }

        findAdministrador.setNombres(administradorDTO.getNombres());
        findAdministrador.setApellidos(administradorDTO.getApellidos());
        findAdministrador.setNumeroDocumento(administradorDTO.getNumeroDocumento());
        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setId(administradorDTO.getTipoDocumentoId());
        findAdministrador.setTipoDocumento(tipoDocumento);
        findAdministrador.setTelefono(administradorDTO.getTelefono());
        findAdministrador.setDireccion(administradorDTO.getDireccion());
        findAdministrador.setFechaContratacion(administradorDTO.getFechaContratacion());
        Usuario usuario = new Usuario();
        usuario.setId(administradorDTO.getUsuarioId());
        Administrador updatedAdministrador = administradorRepository.save(administradorMapper.mapToAdministrador(administradorDTO));
        return administradorMapper.mapToAdministradorDTO(updatedAdministrador);
    }

    @Transactional
    @Override
    public void deleteAdministrador(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Administrador findAdministrador = administradorRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new RuntimeException("No existe un administrador con el id ingresado"));
        findAdministrador.setEstado(false); //borrado logico
        usuarioService.eliminar(findAdministrador.getUsuario().getId());
        findAdministrador.setUsuario(null);
        administradorRepository.save(findAdministrador);
    }
}
