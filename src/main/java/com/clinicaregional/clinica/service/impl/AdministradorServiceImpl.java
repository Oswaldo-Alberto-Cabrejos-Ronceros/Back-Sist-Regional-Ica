package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.AdministradorDTO;
import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.dto.SeguroDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.RegisterAdministradorRequest;
import com.clinicaregional.clinica.dto.response.MyInfoAdministrador;
import com.clinicaregional.clinica.entity.Administrador;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.AdministradorMapper;
import com.clinicaregional.clinica.repository.AdministradorRepository;
import com.clinicaregional.clinica.service.AdministradorService;
import com.clinicaregional.clinica.service.S3ServicePublic;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdministradorServiceImpl implements AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final AdministradorMapper administradorMapper;
    private final UsuarioService usuarioService;
    private final FiltroEstado filtroEstado;
    private final S3ServicePublic s3Service;

    @Autowired
    public AdministradorServiceImpl(AdministradorRepository administradorRepository,
                                    AdministradorMapper administradorMapper, UsuarioService usuarioService, FiltroEstado filtroEstado, S3ServicePublic s3Service) {
        this.administradorRepository = administradorRepository;
        this.administradorMapper = administradorMapper;
        this.usuarioService = usuarioService;
        this.filtroEstado = filtroEstado;
        this.s3Service = s3Service;
    }

    @Transactional(readOnly = true)
    @Override
    public List<AdministradorDTO> listarAdministradores() {
        filtroEstado.activarFiltroEstado(true);
        List<AdministradorDTO> administradores = administradorRepository.findAll().stream().map(administradorMapper::mapToAdministradorDTO)
                .toList();
        return administradores.stream().map(this::agregarUrlImage).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AdministradorDTO> getAdministradorById(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Optional<AdministradorDTO> administradorDTO = administradorRepository.findByIdAndEstadoIsTrue(id).map(administradorMapper::mapToAdministradorDTO);
        return administradorDTO.map(this::agregarUrlImage);
    }

    @Transactional(readOnly = true)
    @Override
    public MyInfoAdministrador getMyInfoAdministrador(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Administrador administrador = administradorRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro Administrador con el id: " + id));
        if (administrador.getImagenUrl() != null) {
            String imageUrl = s3Service.generarUrlPublico(administrador.getImagenUrl());
            administrador.setImagenUrl(imageUrl);
        }
        return administradorMapper.mapToMyInfoAdministrador(administrador);
    }

    @Transactional
    @Override
    public AdministradorDTO createAdministrador(RegisterAdministradorRequest registerAdministradorRequest, MultipartFile imagen) {
        filtroEstado.activarFiltroEstado(true);
        if (administradorRepository
                .existsByNumeroDocumento(registerAdministradorRequest.getAdministrador().getNumeroDocumento())) {
            throw new DuplicateResourceException("Ya existe un administrador con el numero de documento ingresado");
        }

        // Establecer el rol por defecto (ADMIN)
        registerAdministradorRequest.getUsuario().setRol(new RolDTO(2L, "ADMINISTRADOR"));

        UsuarioDTO usuarioGuardado = usuarioService.guardar(registerAdministradorRequest.getUsuario());

        registerAdministradorRequest.getAdministrador().setUsuarioId(usuarioGuardado.getId());
        Administrador administrador = administradorMapper.mapToAdministrador(registerAdministradorRequest.getAdministrador());

        if (imagen != null) {
            String key = s3Service.subirArchivo(imagen, "administrador" + administrador.getNombres());
            administrador.setImagenUrl(key);
        }
        Administrador savedAdministrador = administradorRepository
                .save(administrador);

        return this.agregarUrlImage(administradorMapper.mapToAdministradorDTO(savedAdministrador));
    }

    @Transactional
    @Override
    public AdministradorDTO updateAdministrador(Long id, AdministradorDTO administradorDTO, MultipartFile imagen) {
        filtroEstado.activarFiltroEstado(true);
        Administrador findAdministrador = administradorRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un administrador con el id ingresado"));

        if (administradorRepository.existsByNumeroDocumento(administradorDTO.getNumeroDocumento())) {
            throw new DuplicateResourceException("Ya existe un administrador con el numero de documento ingresado");
        }
        if (administradorRepository.existsByUsuario_Id(administradorDTO.getUsuarioId())) {
            throw new DuplicateResourceException("Ya existe un administrador con el usuario ingresado");
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
        Administrador administrador = administradorMapper.mapToAdministrador(administradorDTO);

        if (imagen != null) {
            if (administrador.getImagenUrl() != null) {
                s3Service.eliminarArchivo(administrador.getImagenUrl());
            }
            String key = s3Service.subirArchivo(imagen, "administrador" + administrador.getNombres());
            administrador.setImagenUrl(key);
        }

        Administrador updatedAdministrador = administradorRepository
                .save(administrador);

        return this.agregarUrlImage(administradorMapper.mapToAdministradorDTO(updatedAdministrador));
    }

    @Transactional
    @Override
    public void deleteAdministrador(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Administrador findAdministrador = administradorRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un administrador con el id ingresado"));
        findAdministrador.setEstado(false); // borrado logico
        usuarioService.eliminar(findAdministrador.getUsuario().getId());
        findAdministrador.setUsuario(null);
        if (findAdministrador.getImagenUrl() != null) {
            s3Service.eliminarArchivo(findAdministrador.getImagenUrl());
        }
        administradorRepository.save(findAdministrador);
    }

    //funcion para obtener el url
    private AdministradorDTO agregarUrlImage(AdministradorDTO administradorDTO) {
        if (administradorDTO.getImagenUrl() != null) {
            String imageUrl = s3Service.generarUrlPublico(administradorDTO.getImagenUrl());
            administradorDTO.setImagenUrl(imageUrl);
        }
        return administradorDTO;
    }
}
