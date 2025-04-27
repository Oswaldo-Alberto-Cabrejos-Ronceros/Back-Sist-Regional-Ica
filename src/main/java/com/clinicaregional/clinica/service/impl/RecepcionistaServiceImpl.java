package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.request.RecepcionistaRequest;
import com.clinicaregional.clinica.dto.response.RecepcionistaResponse;
import com.clinicaregional.clinica.entity.Recepcionista;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.mapper.RecepcionistaMapper;
import com.clinicaregional.clinica.repository.RecepcionistaRepository;
import com.clinicaregional.clinica.service.RecepcionistaService;
import com.clinicaregional.clinica.service.TipoDocumentoService;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.util.FiltroEstado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecepcionistaServiceImpl extends FiltroEstado implements RecepcionistaService {
    private final RecepcionistaRepository recepcionistaRepository;
    private final RecepcionistaMapper recepcionistaMapper;

    @Transactional(readOnly = true)
    @Override
    public List<RecepcionistaResponse> listar() {
        activarFiltroEstado(true);
        return recepcionistaRepository.findAll().stream().map(recepcionistaMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<RecepcionistaResponse> obtenerPorId(Long id) {
        activarFiltroEstado(true);
        return recepcionistaRepository.findByIdAndEstadoIsTrue(id).map(recepcionistaMapper::toResponse);
    }

    @Transactional
    @Override
    public RecepcionistaResponse guardar(RecepcionistaRequest recepcionistaRequest) {
        activarFiltroEstado(true);
        if (recepcionistaRepository.existsByNumeroDocumento(recepcionistaRequest.getNumeroDocumento())) {
            throw new RuntimeException("Recepcionista ya existe con el numero de documento ingresado");
        }
        Recepcionista recepcionista = recepcionistaMapper.toEntity(recepcionistaRequest);

        if(recepcionistaRepository.existsByUsuario(recepcionista.getUsuario())) {
            throw new RuntimeException("Usuario ya existe con el usuario ingresado");
        }

        Recepcionista savedRecepcionista = recepcionistaRepository.save(recepcionista);
        return recepcionistaMapper.toResponse(savedRecepcionista) ;
    }

    @Transactional
    @Override
    public RecepcionistaResponse actualizar(Long id, RecepcionistaRequest recepcionistaRequest) {
        activarFiltroEstado(true);
        Recepcionista recepcionistaExistente = recepcionistaRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new RuntimeException("Recepcionista no encontrada"));

        if (recepcionistaRepository.existsByNumeroDocumento(recepcionistaRequest.getNumeroDocumento())) {
            throw new RuntimeException("Recepcionista ya existe con el dni ingresado");
        }

        Recepcionista recepcionista = recepcionistaMapper.toEntity(recepcionistaRequest);

        if(recepcionistaRepository.existsByUsuario(recepcionista.getUsuario())) {
            throw new RuntimeException("Usuario ya existe con el usuario ingresado");
        }

        recepcionistaExistente.setNombres(recepcionista.getNombres());
        recepcionistaExistente.setApellidos(recepcionista.getApellidos());
        recepcionistaExistente.setNumeroDocumento(recepcionista.getNumeroDocumento());
        recepcionistaExistente.setTelefono(recepcionista.getTelefono());
        recepcionistaExistente.setDireccion(recepcionista.getDireccion());
        recepcionistaExistente.setTurnoTrabajo(recepcionista.getTurnoTrabajo());
        recepcionistaExistente.setFechaContratacion(recepcionista.getFechaContratacion());
        recepcionistaExistente.setTipoDocumento(recepcionista.getTipoDocumento());
        recepcionistaExistente.setUsuario(recepcionista.getUsuario());

        Recepcionista updatedRecepcionista = recepcionistaRepository.save(recepcionista);

        return recepcionistaMapper.toResponse(updatedRecepcionista);
    }

    @Transactional
    @Override
    public void eliminar(Long id) {
        activarFiltroEstado(true);
        Recepcionista recepcionista = recepcionistaRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new RuntimeException("Recepcionista no encontrada"));
        recepcionista.setEstado(false);
        recepcionistaRepository.save(recepcionista);
    }
}
