package com.clinicaregional.clinica.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.clinicaregional.clinica.dto.request.HistorialClinicoRequest;
import com.clinicaregional.clinica.dto.response.HistorialClinicoResponse;
import com.clinicaregional.clinica.service.HistorialClinicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/historial-clinico")
public class HistorialClinicoController {

    private final HistorialClinicoService historialClinicoService;

    @Autowired
    public HistorialClinicoController(HistorialClinicoService historialClinicoService) {
        this.historialClinicoService = historialClinicoService;
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

}
