package com.clinicaregional.clinica.service;
import com.clinicaregional.clinica.dto.ResultadoArchivoDTO;
import com.clinicaregional.clinica.dto.request.ResultadoRequest;
import com.clinicaregional.clinica.dto.response.ResultadoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;



public interface ResultadoService {

    ResultadoResponse crear(ResultadoRequest resultadoRequest, MultipartFile archivo);

    ResultadoArchivoDTO recuperarArchivoByResultadoId(Long resultadoId);

    ResultadoResponse agregarArchivoResultado(Long resultadoId, MultipartFile archivo);

    List<ResultadoResponse> obtenerPorCita(Long citaId);

    List<ResultadoResponse> listarPorHistorialClinicoDePaciente(Long Id);

    ResultadoResponse actualizar(Long id, ResultadoRequest resultadoRequest);

    void eliminar(Long id);

    
}
