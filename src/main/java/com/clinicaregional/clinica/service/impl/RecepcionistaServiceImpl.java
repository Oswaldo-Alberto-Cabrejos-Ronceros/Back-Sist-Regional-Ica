package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.AdministradorDTO;
import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.RecepcionistaRequest;
import com.clinicaregional.clinica.dto.request.UsuarioRequestDTO;
import com.clinicaregional.clinica.dto.response.MyInfoRecepcionista;
import com.clinicaregional.clinica.dto.response.RecepcionistaResponse;
import com.clinicaregional.clinica.entity.Recepcionista;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.RecepcionistaMapper;
import com.clinicaregional.clinica.repository.RecepcionistaRepository;
import com.clinicaregional.clinica.repository.TipoDocumentoRepository;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.service.RecepcionistaService;

import com.clinicaregional.clinica.service.RolService;

import com.clinicaregional.clinica.service.S3ServicePublic;

import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.util.FiltroEstado;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecepcionistaServiceImpl implements RecepcionistaService {


        private final RecepcionistaRepository recepcionistaRepository;
        private final TipoDocumentoRepository tipoDocumentoRepository;
        private final UsuarioRepository usuarioRepository;
        private final RecepcionistaMapper recepcionistaMapper;
        private final UsuarioService usuarioService;
        private final RolService rolService;
        private final FiltroEstado filtroEstado;

        @Autowired
        public RecepcionistaServiceImpl(
                        RecepcionistaRepository recepcionistaRepository,
                        TipoDocumentoRepository tipoDocumentoRepository,
                        UsuarioRepository usuarioRepository,
                        RecepcionistaMapper recepcionistaMapper,
                        UsuarioService usuarioService,
                        RolService rolService,
                        FiltroEstado filtroEstado) {
                this.recepcionistaRepository = recepcionistaRepository;
                this.tipoDocumentoRepository = tipoDocumentoRepository;
                this.usuarioRepository = usuarioRepository;
                this.recepcionistaMapper = recepcionistaMapper;
                this.usuarioService = usuarioService;
                this.rolService = rolService;
                this.filtroEstado = filtroEstado;
        }

        @Transactional(readOnly = true)
        @Override
        public List<RecepcionistaResponse> listar() {
                filtroEstado.activarFiltroEstado(true);
                return recepcionistaRepository.findAll()
                                .stream()
                                .map(recepcionistaMapper::toResponse)
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        @Override
        public Optional<RecepcionistaResponse> obtenerPorId(Long id) {
                return recepcionistaRepository.findByIdAndEstadoIsTrue(id)
                                .map(recepcionistaMapper::toResponse);
        }

        @Transactional(readOnly = true)
        @Override
        public MyInfoRecepcionista obtenerMyInfoRecepcionista(Long id) {
                Recepcionista recepcionista = recepcionistaRepository.findByIdAndEstadoIsTrue(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "No se encontro recepcionista con el id: " + id));
                return recepcionistaMapper.toMyInfoRecepcionista(recepcionista);
        }

        @Transactional
        @Override
        public RecepcionistaResponse guardar(RecepcionistaRequest request) {
                filtroEstado.activarFiltroEstado(true);

                if (recepcionistaRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
                        throw new RuntimeException("Ya existe un recepcionista con el mismo número de documento");
                }
                // Obtener rol con validación
                RolDTO rolRecepcionista = rolService.obtenerRolPorNombre("RECEPCIONISTA")
                                .orElseThrow(() -> new IllegalStateException(
                                                "Rol RECEPCIONISTA no encontrado en el sistema"));

                // Crear usuario
                UsuarioRequestDTO newUsuario = new UsuarioRequestDTO();
                newUsuario.setCorreo(request.getCorreo());
                newUsuario.setPassword(request.getPassword());
                newUsuario.setRol(rolRecepcionista);

                UsuarioDTO usuarioDTO = usuarioService.guardar(newUsuario);

                Usuario usuario = new Usuario();
                usuario.setId(usuarioDTO.getId());

                TipoDocumento tipoDocumento = tipoDocumentoRepository
                                .findByIdAndEstadoIsTrue(request.getTipoDocumentoId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Tipo de documento no encontrado con id: "
                                                                + request.getTipoDocumentoId()));

                Recepcionista recepcionista = Recepcionista.builder()
                                .nombres(request.getNombres())
                                .apellidos(request.getApellidos())
                                .numeroDocumento(request.getNumeroDocumento())
                                .telefono(request.getTelefono())
                                .direccion(request.getDireccion())
                                .imagenUrl(request.getImagenUrl())
                                .turnoTrabajo(request.getTurnoTrabajo())
                                .fechaContratacion(request.getFechaContratacion())
                                .usuario(usuario)
                                .tipoDocumento(tipoDocumento)
                                .estado(true)
                                .build();
                return recepcionistaMapper.toResponse(recepcionistaRepository.save(recepcionista));
        }

        @Transactional
        @Override
        public RecepcionistaResponse actualizar(Long id, RecepcionistaRequest request) {
                filtroEstado.activarFiltroEstado(true);

                Recepcionista recepcionistaExistente = recepcionistaRepository.findByIdAndEstadoIsTrue(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Recepcionista no encontrada con id: " + id));

                boolean documentoDuplicado = recepcionistaRepository
                                .existsByNumeroDocumento(request.getNumeroDocumento())
                                && !recepcionistaExistente.getNumeroDocumento()
                                                .equalsIgnoreCase(request.getNumeroDocumento());

                if (documentoDuplicado) {
                        throw new DuplicateResourceException(
                                        "Ya existe un recepcionista con el número de documento ingresado.");
                }

                Usuario usuario = usuarioRepository.findByIdAndEstadoIsTrue(request.getUsuarioId())
                                .orElseThrow(
                                                () -> new ResourceNotFoundException("Usuario no encontrado con id: "
                                                                + request.getUsuarioId()));

                TipoDocumento tipoDocumento = tipoDocumentoRepository
                                .findByIdAndEstadoIsTrue(request.getTipoDocumentoId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Tipo de documento no encontrado con id: "
                                                                + request.getTipoDocumentoId()));

                recepcionistaExistente.setNombres(request.getNombres());
                recepcionistaExistente.setApellidos(request.getApellidos());
                recepcionistaExistente.setNumeroDocumento(request.getNumeroDocumento());
                recepcionistaExistente.setTelefono(request.getTelefono());
                recepcionistaExistente.setDireccion(request.getDireccion());
                recepcionistaExistente.setTurnoTrabajo(request.getTurnoTrabajo());
                recepcionistaExistente.setFechaContratacion(request.getFechaContratacion());
                recepcionistaExistente.setTipoDocumento(tipoDocumento);
                recepcionistaExistente.setUsuario(usuario);

                return recepcionistaMapper.toResponse(recepcionistaRepository.save(recepcionistaExistente));
        }

        @Transactional
        @Override
        public void eliminar(Long id) {
                filtroEstado.activarFiltroEstado(true);
                Recepcionista recepcionista = recepcionistaRepository.findByIdAndEstadoIsTrue(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Recepcionista no encontrada con ID: " + id));

                // 1. Primero marca el recepcionista como inactivo
                recepcionista.setEstado(false);
                recepcionistaRepository.save(recepcionista);

                // 2. Manejo del usuario asociado (si existe)
                if (recepcionista.getUsuario() != null) {
                        Long usuarioId = recepcionista.getUsuario().getId();

                        try {
                                // 3. Elimina el usuario en una nueva transacción
                                usuarioService.eliminarUsuarioSinRelaciones(usuarioId);

                                // 4. Actualiza el recepcionista para establecer usuario_id como null
                                recepcionista.setUsuario(null);
                                recepcionistaRepository.save(recepcionista);
                        } catch (Exception e) {
                                // Loggear el error pero continuar
                                System.err.println("Error al eliminar usuario asociado: " + e.getMessage());
                        }
                }
        }
/*
    private final RecepcionistaRepository recepcionistaRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecepcionistaMapper recepcionistaMapper;
    private final UsuarioService usuarioService;
    private final FiltroEstado filtroEstado;
    private final S3ServicePublic s3Service;

    @Autowired
    public RecepcionistaServiceImpl(
            RecepcionistaRepository recepcionistaRepository,
            TipoDocumentoRepository tipoDocumentoRepository,
            UsuarioRepository usuarioRepository,
            RecepcionistaMapper recepcionistaMapper,
            UsuarioService usuarioService,
            FiltroEstado filtroEstado, S3ServicePublic s3Service) {
        this.recepcionistaRepository = recepcionistaRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.recepcionistaMapper = recepcionistaMapper;
        this.usuarioService = usuarioService;
        this.filtroEstado = filtroEstado;
        this.s3Service = s3Service;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RecepcionistaResponse> listar() {
        filtroEstado.activarFiltroEstado(true);

        List<RecepcionistaResponse> recepcionistas = recepcionistaRepository.findAll()
                .stream()
                .map(recepcionistaMapper::toResponse)
                .toList();

        return recepcionistas.stream().map(this::agregarUrlImage).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<RecepcionistaResponse> obtenerPorId(Long id) {

        Optional<RecepcionistaResponse> recepcionistaResponse = recepcionistaRepository.findByIdAndEstadoIsTrue(id)
                .map(recepcionistaMapper::toResponse);

        return recepcionistaResponse.map(this::agregarUrlImage);
    }

    @Transactional(readOnly = true)
    @Override
    public MyInfoRecepcionista obtenerMyInfoRecepcionista(Long id) {
        Recepcionista recepcionista = recepcionistaRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new ResourceNotFoundException(
                "No se encontro recepcionista con el id: " + id
        ));
        return recepcionistaMapper.toMyInfoRecepcionista(recepcionista);
    }

    @Transactional
    @Override
    public RecepcionistaResponse guardar(RecepcionistaRequest request, MultipartFile imagen) {
        filtroEstado.activarFiltroEstado(true);

        if (recepcionistaRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
            throw new RuntimeException("Ya existe un recepcionista con el mismo número de documento");
        }

        //guardamos usuario
        UsuarioRequestDTO newUsuario = new UsuarioRequestDTO();
        newUsuario.setCorreo(request.getCorreo());
        newUsuario.setPassword(request.getPassword());

        RolDTO rolRecepcinista = new RolDTO();
        rolRecepcinista.setId(3L);//ROL RECEPCIONISTA
        newUsuario.setRol(rolRecepcinista);

        UsuarioDTO usuarioDTO = usuarioService.guardar(newUsuario);
        Usuario usuario = new Usuario();
        usuario.setId(usuarioDTO.getId());

        TipoDocumento tipoDocumento = tipoDocumentoRepository.findByIdAndEstadoIsTrue(request.getTipoDocumentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de documento no encontrado con id: " + request.getTipoDocumentoId()));

        Recepcionista recepcionista = Recepcionista.builder()
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .numeroDocumento(request.getNumeroDocumento())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .turnoTrabajo(request.getTurnoTrabajo())
                .fechaContratacion(request.getFechaContratacion())
                .usuario(usuario)
                .tipoDocumento(tipoDocumento)
                .estado(true)
                .build();

        if (imagen != null) {
            String key = s3Service.subirArchivo(imagen, "recepcionista" + recepcionista.getNombres());
            recepcionista.setImagenUrl(key);
        }

        Recepcionista recepcionistaSaved = recepcionistaRepository.save(recepcionista);
        return this.agregarUrlImage(recepcionistaMapper.toResponse(recepcionistaSaved));
    }

    @Transactional
    @Override
    public RecepcionistaResponse actualizar(Long id, RecepcionistaRequest request, MultipartFile imagen) {
        filtroEstado.activarFiltroEstado(true);

        Recepcionista recepcionistaExistente = recepcionistaRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recepcionista no encontrada con id: " + id));

        boolean documentoDuplicado = recepcionistaRepository.existsByNumeroDocumento(request.getNumeroDocumento())
                && !recepcionistaExistente.getNumeroDocumento().equalsIgnoreCase(request.getNumeroDocumento());

        if (documentoDuplicado) {
            throw new DuplicateResourceException("Ya existe un recepcionista con el número de documento ingresado.");
        }

        Usuario usuario = usuarioRepository.findByIdAndEstadoIsTrue(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + request.getUsuarioId()));

        TipoDocumento tipoDocumento = tipoDocumentoRepository.findByIdAndEstadoIsTrue(request.getTipoDocumentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de documento no encontrado con id: " + request.getTipoDocumentoId()));

        recepcionistaExistente.setNombres(request.getNombres());
        recepcionistaExistente.setApellidos(request.getApellidos());
        recepcionistaExistente.setNumeroDocumento(request.getNumeroDocumento());
        recepcionistaExistente.setTelefono(request.getTelefono());
        recepcionistaExistente.setDireccion(request.getDireccion());
        recepcionistaExistente.setTurnoTrabajo(request.getTurnoTrabajo());
        recepcionistaExistente.setFechaContratacion(request.getFechaContratacion());
        recepcionistaExistente.setTipoDocumento(tipoDocumento);
        recepcionistaExistente.setUsuario(usuario);

        if (imagen != null) {
            if (recepcionistaExistente.getImagenUrl() != null) {
                s3Service.eliminarArchivo(recepcionistaExistente.getImagenUrl());
            }
            String key = s3Service.subirArchivo(imagen, "recepcionista" + recepcionistaExistente.getNombres());
            recepcionistaExistente.setImagenUrl(key);
        }


        Recepcionista recepcionista = recepcionistaRepository.save(recepcionistaExistente);

        return this.agregarUrlImage(recepcionistaMapper.toResponse(recepcionista));
    }


    @Transactional
    @Override
    public void eliminar(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Recepcionista recepcionista = recepcionistaRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new RuntimeException("Recepcionista no encontrada"));
        recepcionista.setEstado(false); //borrado
        usuarioService.eliminar(recepcionista.getUsuario().getId());
        recepcionista.setUsuario(null);
        if (recepcionista.getImagenUrl() != null) {
            s3Service.eliminarArchivo(recepcionista.getImagenUrl());
        }
        recepcionistaRepository.save(recepcionista);
    }

    //funcion para obtener el url
    private RecepcionistaResponse agregarUrlImage(RecepcionistaResponse recepcionistaResponse) {
        if (recepcionistaResponse.getImagenUrl() != null) {
            String imageUrl = s3Service.generarUrlPublico(recepcionistaResponse.getImagenUrl());
            recepcionistaResponse.setImagenUrl(imageUrl);
        }
        return recepcionistaResponse;
    }
*/

}
