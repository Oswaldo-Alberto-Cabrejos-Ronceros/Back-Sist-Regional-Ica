package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.entity.Recepcionista;
import com.clinicaregional.clinica.repository.RecepcionistaRepository;
import com.clinicaregional.clinica.service.RecepcionistaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecepcionistaServiceImpl implements RecepcionistaService {
    private final RecepcionistaRepository recepcionistaRepository;

    @Override
    public List<Recepcionista> listar() {
        return recepcionistaRepository.findAll();
    }

    @Override
    public Recepcionista obtenerPorId(Long id) {
        return recepcionistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recepcionista no encontrado"));
    }

    @Override
    public Recepcionista guardar(Recepcionista recepcionista) {
        return recepcionistaRepository.save(recepcionista);
    }

    @Override
    public Recepcionista actualizar(Long id, Recepcionista recepcionista) {
        Recepcionista recepcionistaExistente = obtenerPorId(id);

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

    @Override
    public void eliminar(Long id) {
        Recepcionista recepcionista = obtenerPorId(id);
        recepcionistaRepository.delete(recepcionista);
    }
}
