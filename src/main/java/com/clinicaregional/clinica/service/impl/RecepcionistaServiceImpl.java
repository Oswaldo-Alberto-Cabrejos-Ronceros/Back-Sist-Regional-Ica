package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.RecepcionistaRequest;
import com.clinicaregional.clinica.dto.request.RecepcionistaUpdateDTO;
import com.clinicaregional.clinica.dto.request.UsuarioRequestDTO;
import com.clinicaregional.clinica.dto.response.MyInfoRecepcionista;
import com.clinicaregional.clinica.dto.response.RecepcionistaResponse;
import com.clinicaregional.clinica.entity.Recepcionista;
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
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.util.FiltroEstado;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

                Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                                .orElseThrow(() -> new RuntimeException("Usuario recién creado no encontrado"));

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
        public RecepcionistaResponse actualizar(Long id, RecepcionistaUpdateDTO request) {
                filtroEstado.activarFiltroEstado(true);

                Recepcionista recepcionista = recepcionistaRepository.findByIdAndEstadoIsTrue(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Recepcionista no encontrada con ID: " + id));

                Usuario usuario = recepcionista.getUsuario();

                // Validar número de documento si cambió
                if (request.getNumeroDocumento() != null &&
                                !request.getNumeroDocumento().equalsIgnoreCase(recepcionista.getNumeroDocumento()) &&
                                recepcionistaRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
                        throw new DuplicateResourceException(
                                        "Ya existe un recepcionista con el número de documento ingresado.");
                }

                // Validar correo si cambió
                if (request.getCorreo() != null &&
                                !request.getCorreo().equalsIgnoreCase(usuario.getCorreo()) &&
                                usuarioRepository.existsByCorreo(request.getCorreo())) {
                        throw new DuplicateResourceException("Ya existe un usuario con el correo ingresado.");
                }

                // Actualizar correo del usuario si vino
                if (request.getCorreo() != null && !request.getCorreo().isBlank()) {
                        usuario.setCorreo(request.getCorreo());
                        usuarioRepository.save(usuario);
                }

                // Actualizar tipo de documento si cambió
                if (request.getTipoDocumentoId() != null &&
                                !recepcionista.getTipoDocumento().getId().equals(request.getTipoDocumentoId())) {
                        TipoDocumento tipoDocumento = tipoDocumentoRepository
                                        .findByIdAndEstadoIsTrue(request.getTipoDocumentoId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Tipo de documento no encontrado con ID: "
                                                                        + request.getTipoDocumentoId()));
                        recepcionista.setTipoDocumento(tipoDocumento);
                }

                // Actualizar campos si vienen
                if (request.getNombres() != null && !request.getNombres().isBlank()) {
                        recepcionista.setNombres(request.getNombres());
                }

                if (request.getApellidos() != null && !request.getApellidos().isBlank()) {
                        recepcionista.setApellidos(request.getApellidos());
                }

                if (request.getNumeroDocumento() != null && !request.getNumeroDocumento().isBlank()) {
                        recepcionista.setNumeroDocumento(request.getNumeroDocumento());
                }

                if (request.getTelefono() != null && !request.getTelefono().isBlank()) {
                        recepcionista.setTelefono(request.getTelefono());
                }

                if (request.getDireccion() != null && !request.getDireccion().isBlank()) {
                        recepcionista.setDireccion(request.getDireccion());
                }

                if (request.getImagenUrl() != null && !request.getImagenUrl().isBlank()) {
                        recepcionista.setImagenUrl(request.getImagenUrl());
                }

                if (request.getTurnoTrabajo() != null) {
                        recepcionista.setTurnoTrabajo(request.getTurnoTrabajo());
                }

                // Guardar cambios
                return recepcionistaMapper.toResponse(recepcionistaRepository.save(recepcionista));
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

}