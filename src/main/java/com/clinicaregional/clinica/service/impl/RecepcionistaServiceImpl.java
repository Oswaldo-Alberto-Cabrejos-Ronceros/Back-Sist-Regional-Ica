package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.RecepcionistaRequest;
import com.clinicaregional.clinica.dto.request.UsuarioRequestDTO;
import com.clinicaregional.clinica.dto.response.RecepcionistaResponse;
import com.clinicaregional.clinica.entity.Recepcionista;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.mapper.RecepcionistaMapper;
import com.clinicaregional.clinica.repository.RecepcionistaRepository;
import com.clinicaregional.clinica.repository.TipoDocumentoRepository;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.service.RecepcionistaService;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.util.FiltroEstado;
import lombok.RequiredArgsConstructor;

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
    private final FiltroEstado filtroEstado;

    @Autowired
    public RecepcionistaServiceImpl(
            RecepcionistaRepository recepcionistaRepository,
            TipoDocumentoRepository tipoDocumentoRepository,
            UsuarioRepository usuarioRepository,
            RecepcionistaMapper recepcionistaMapper,
            UsuarioService usuarioService,
            FiltroEstado filtroEstado) {
        this.recepcionistaRepository = recepcionistaRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.recepcionistaMapper = recepcionistaMapper;
        this.usuarioService = usuarioService;
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
        filtroEstado.activarFiltroEstado(true);
        return recepcionistaRepository.findByIdAndEstadoIsTrue(id)
                .map(recepcionistaMapper::toResponse);
    }

    @Transactional
    @Override
    public RecepcionistaResponse guardar(RecepcionistaRequest request) {
        filtroEstado.activarFiltroEstado(true);

        if (recepcionistaRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
            throw new RuntimeException("Ya existe un recepcionista con el mismo número de documento");
        }
        //guardamos usuario
        UsuarioRequestDTO newUsuario= new UsuarioRequestDTO();
        newUsuario.setCorreo(request.getCorreo());
        newUsuario.setPassword(request.getPassword());
        RolDTO rolRecepcinista = new RolDTO();
        rolRecepcinista.setId(3L);//ROL RECEPCIONISTA
        newUsuario.setRol(rolRecepcinista);

        UsuarioDTO usuarioDTO = usuarioService.guardar(newUsuario);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioDTO.getId());

        TipoDocumento tipoDocumento = tipoDocumentoRepository.findByIdAndEstadoIsTrue(request.getTipoDocumentoId())
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado con id: " + request.getTipoDocumentoId()));

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
        return recepcionistaMapper.toResponse(recepcionistaRepository.save(recepcionista));
    }

    @Transactional
    @Override
    public RecepcionistaResponse actualizar(Long id, RecepcionistaRequest request) {
        filtroEstado.activarFiltroEstado(true);

        Recepcionista recepcionistaExistente = recepcionistaRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new RuntimeException("Recepcionista no encontrada con id: " + id));

        if (recepcionistaRepository.existsByNumeroDocumento(request.getNumeroDocumento()) &&
                !recepcionistaExistente.getNumeroDocumento().equals(request.getNumeroDocumento())) {
            throw new RuntimeException("Ya existe un recepcionista con el número de documento ingresado.");
        }

        Usuario usuario = usuarioRepository.findByIdAndEstadoIsTrue(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + request.getUsuarioId()));

        TipoDocumento tipoDocumento = tipoDocumentoRepository.findByIdAndEstadoIsTrue(request.getTipoDocumentoId())
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado con id: " + request.getTipoDocumentoId()));

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
        Recepcionista recepcionista = recepcionistaRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new RuntimeException("Recepcionista no encontrada"));
        recepcionista.setEstado(false); //borrado
        usuarioService.eliminar(recepcionista.getUsuario().getId());
        recepcionista.setUsuario(null);
        recepcionistaRepository.save(recepcionista);
    }
}
