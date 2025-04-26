package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.entity.Recepcionista;
import java.util.List;

public interface RecepcionistaService {
    List<Recepcionista> listar();
    Recepcionista obtenerPorId(Long id);
    Recepcionista guardar(Recepcionista recepcionista);
    Recepcionista actualizar(Long id, Recepcionista recepcionista);
    void eliminar(Long id);
}
