package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
import com.clinicaregional.clinica.dto.response.PacienteResponseDTO;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.enums.EstadoCita;

import java.util.List;

public interface CitaService {

    CitaResponse registrar(CitaRequest request);

    CitaResponse obtenerPorId(Long id);

    List<CitaResponse> listarTodas();

    CitaResponse actualizar(Long id, CitaRequest request);

    void eliminar(Long id);

    CitaResponse confirmarCita(Long citaId);

    CitaResponse cancelarCita(Long citaId);

    CitaResponse atenderCita(Long citaId);

    CitaResponse reprogramarCita(Long citaId, CitaRequest nuevaCitaRequest);

    List<PacienteResponseDTO> obtenerPacientesPorMedicoConCitasConfirmadasOAtendidas(Long medicoId);
}
