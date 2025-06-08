package com.clinicaregional.clinica.service;
import com.clinicaregional.clinica.dto.request.ResultadoRequest;
import com.clinicaregional.clinica.dto.response.ResultadoResponse;
import java.util.List;



public interface ResultadoService {

    ResultadoResponse crear(ResultadoRequest resultadoRequest);

    List<ResultadoResponse> obtenerPorCita(Long citaId);

    List<ResultadoResponse> listarPorHistorialClinicoDePaciente(Long Id);

    ResultadoResponse actualizar(Long id, ResultadoRequest resultadoRequest);

    void eliminar(Long id);

    
}
