package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.request.HistorialClinicoRequest;
import com.clinicaregional.clinica.dto.response.HistorialClinicoResponse;
import com.clinicaregional.clinica.dto.response.ResultadoResponse;
import com.clinicaregional.clinica.service.HistorialClinicoService;
import com.clinicaregional.clinica.service.ResultadoService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/historial-clinico")
public class HistorialClinicoController {

    private final HistorialClinicoService historialClinicoService;
    private final ResultadoService resultadoService;

    @Autowired
    public HistorialClinicoController(HistorialClinicoService historialClinicoService,
            ResultadoService resultadoService) {
        this.historialClinicoService = historialClinicoService;
        this.resultadoService = resultadoService;
    }

    @PostMapping
    public ResponseEntity<HistorialClinicoResponse> crear(
            @RequestBody HistorialClinicoRequest historialClinicoRequest) {
        HistorialClinicoResponse historialClinicoCreated = historialClinicoService.crear(historialClinicoRequest);
        return ResponseEntity.status(201).body(historialClinicoCreated);
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<HistorialClinicoResponse> obtenerPorPacienteId(@PathVariable Long pacienteId) {
        HistorialClinicoResponse historialClinico = historialClinicoService.obtenerPorPacienteId(pacienteId);
        return ResponseEntity.ok(historialClinico);
    }

    @GetMapping("/{historialId}/resultados")
    public ResponseEntity<List<ResultadoResponse>> listarResultadosPorHistorial(@PathVariable Long historialId) {
        List<ResultadoResponse> resultados = resultadoService.listarPorHistorialClinico(historialId);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/paciente/{pacienteId}/resultados")
    public ResponseEntity<List<ResultadoResponse>> obtenerResultadosPorPaciente(@PathVariable Long pacienteId) {
        List<ResultadoResponse> resultados = resultadoService.listarResultadosPorPacienteId(pacienteId);
        return ResponseEntity.ok(resultados);
    }

}
