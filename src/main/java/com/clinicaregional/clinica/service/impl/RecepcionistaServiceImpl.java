package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.entity.Recepcionista;
import com.clinicaregional.clinica.repository.RecepcionistaRepository;
import com.clinicaregional.clinica.service.RecepcionistaService;
import com.clinicaregional.clinica.util.FiltroEstado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecepcionistaServiceImpl extends FiltroEstado implements RecepcionistaService {
    private final RecepcionistaRepository recepcionistaRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Recepcionista> listar() {
        activarFiltroEstado(true);
        return recepcionistaRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Recepcionista obtenerPorId(Long id) {
        activarFiltroEstado(true);
        return recepcionistaRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new RuntimeException("Recepcionista no encontrado"));
    }

    @Transactional
    @Override
    public Recepcionista guardar(Recepcionista recepcionista) {
        activarFiltroEstado(true);
        if (recepcionistaRepository.existsByNumeroDocumento(recepcionista.getNumeroDocumento())) {
            throw new RuntimeException("Recepcionista ya existe con el dni ingresado");
        }
        return recepcionistaRepository.save(recepcionista);
    }

    @Transactional
    @Override
    public Recepcionista actualizar(Long id, Recepcionista recepcionista) {
        activarFiltroEstado(true);
        Recepcionista recepcionistaExistente = obtenerPorId(id);

        if (recepcionistaRepository.existsByNumeroDocumento(recepcionista.getNumeroDocumento())) {
            throw new RuntimeException("Recepcionista ya existe con el dni ingresado");
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

        return recepcionistaRepository.save(recepcionistaExistente);
    }

    @Transactional
    @Override
    public void eliminar(Long id) {
        activarFiltroEstado(true);
        Recepcionista recepcionista = obtenerPorId(id);
        recepcionista.setEstado(false);
        recepcionistaRepository.save(recepcionista);
    }
}
