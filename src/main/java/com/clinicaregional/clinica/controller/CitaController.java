package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
import com.clinicaregional.clinica.dto.response.PacienteResponseDTO;
import com.clinicaregional.clinica.dto.response.ProximaCitaResponse;
import com.clinicaregional.clinica.service.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    // Registrar una cita
    @PostMapping
    public ResponseEntity<CitaResponse> registrar(@Valid @RequestBody CitaRequest request) {
        return ResponseEntity.status(201).body(citaService.registrar(request));
    }

    // Obtener una cita por ID
    @GetMapping("/{id}")
    public ResponseEntity<CitaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtenerPorId(id));
    }

    // Listar todas las citas
    @GetMapping
    public ResponseEntity<List<CitaResponse>> listarTodas() {
        return ResponseEntity.ok(citaService.listarTodas());
    }

    // Listar citas por médico
     @GetMapping("/citas-medico/{medicoId}")
    public ResponseEntity<List<CitaResponse>> obtenerCitasPorMedico(@PathVariable Long medicoId) {
        return ResponseEntity.ok(citaService.listarPorMedico(medicoId));
    }
    // Listar citas por médico y estado CONFIRMADA
    @GetMapping("/citas-medico-confirmada/{medicoId}")
    public ResponseEntity<List<CitaResponse>> obtenerCitasPorMedicoConfirmadas(@PathVariable Long medicoId) {
        return ResponseEntity.ok(citaService.listarPorMedicoAndEstadoConfirmada(medicoId));
    }
    // Listar citas por médico y estado ATENDIDA
    @GetMapping("/citas-medico-atendida/{medicoId}")
    public ResponseEntity<List<CitaResponse>> obtenerCitasPorMedicoAtendidas(@PathVariable Long medicoId) {
        return ResponseEntity.ok(citaService.listarPorMedicoAndEstadoAtendida(medicoId));
    }
    @GetMapping("/citas-medico-confirmada-dia/{medicoId}")
    public ResponseEntity<List<CitaResponse>> obtenerCitasPorMedicoConfirmadasDia(@PathVariable Long medicoId) {
        return ResponseEntity.ok(citaService.listarPorDiaAndEstadoConfirmada(medicoId));
    }   
    // Actualizar una cita
    @PutMapping("/{id}")
    public ResponseEntity<CitaResponse> actualizar(@PathVariable Long id,
            @Valid @RequestBody CitaRequest request) {
        return ResponseEntity.ok(citaService.actualizar(id, request));
    }

    // Eliminar una cita
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        citaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Confirmar una cita
    @PutMapping("/confirmar/{id}")
    public ResponseEntity<CitaResponse> confirmarCita(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.confirmarCita(id));
    }

    // Cancelar una cita
    @PutMapping("/cancelar/{id}")
    public ResponseEntity<CitaResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.cancelarCita(id));
    }

    // Atender una cita
    @PutMapping("/atender/{id}")
    public ResponseEntity<CitaResponse> atender(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.atenderCita(id));
    }

    @PutMapping("/reprogramar/{id}")
    public ResponseEntity<CitaResponse> reprogramar(@PathVariable Long id, @Valid @RequestBody CitaRequest request) {
        return ResponseEntity.ok(citaService.reprogramarCita(id, request));
    }

    // Obtener pacientes que tuvieron citas CONFIRMADAS o ATENDIDAS con un médico
    @GetMapping("/medico/{medicoId}/pacientes")
    public ResponseEntity<List<PacienteResponseDTO>> obtenerPacientesPorMedico(
            @PathVariable Long medicoId) {
        return ResponseEntity.ok(citaService.obtenerPacientesPorMedicoConCitasConfirmadasOAtendidas(medicoId));
    }

    //endpoint para obtener las citas futuras de un paciente por ID
    @GetMapping("/paciente/{pacienteId}/citas-futuras")
    public ResponseEntity<List<ProximaCitaResponse>> listarCitasFuturas(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(citaService.obtenerCitasFuturasPorPaciente(pacienteId));
    }

}
