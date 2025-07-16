package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.request.ResultadoRequest;
import com.clinicaregional.clinica.dto.response.ResultadoResponse;
import com.clinicaregional.clinica.service.ResultadoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resultados")
public class ResultadoController {

    private final ResultadoService resultadoService;

    @Autowired
    public ResultadoController(ResultadoService resultadoService) {
        this.resultadoService = resultadoService;
    }

    @PostMapping
    public ResponseEntity<ResultadoResponse> crearResultado(@RequestBody ResultadoRequest request) {
        ResultadoResponse creado = resultadoService.crear(request);
        return ResponseEntity.ok(creado);
    }

    @GetMapping("/por-cita/{citaId}")
    public ResponseEntity<List<ResultadoResponse>> obtenerPorCita(@PathVariable Long citaId) {
        List<ResultadoResponse> resultados = resultadoService.obtenerPorCita(citaId);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/por-paciente/{pacienteId}")
    public ResponseEntity<List<ResultadoResponse>> listarPorPaciente(@PathVariable Long pacienteId) {
        List<ResultadoResponse> resultados = resultadoService.listarPorHistorialClinicoDePaciente(pacienteId);
        return ResponseEntity.ok(resultados);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultadoResponse> actualizarResultado(@PathVariable Long id,
            @RequestBody ResultadoRequest request) {
        ResultadoResponse actualizado = resultadoService.actualizar(id, request);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarResultado(@PathVariable Long id) {
        resultadoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
