package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.PacienteAlergiaDTO;
import com.clinicaregional.clinica.service.PacienteAlergiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paciente-alergia")
@Validated
public class PacienteAlergiaController {
    private final PacienteAlergiaService pacienteAlergiaService;

    @Autowired
    public PacienteAlergiaController(PacienteAlergiaService pacienteAlergiaService) {
        this.pacienteAlergiaService = pacienteAlergiaService;
    }

    @GetMapping
    public ResponseEntity<List<PacienteAlergiaDTO>> listarPacientesAlergias() {
        return ResponseEntity.ok(pacienteAlergiaService.listarPacienteAlergias());
    }

    @GetMapping("/paciente/{id}")
    public ResponseEntity<List<PacienteAlergiaDTO>> listarPacientesAlergiasPorPaciente(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteAlergiaService.listarPacienteAlergiasPorPaciente(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteAlergiaDTO> getPacienteAlergiaById(@PathVariable Long id) {
        return pacienteAlergiaService.getPacienteAlergiaById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PacienteAlergiaDTO> createPacienteAlergia(@RequestBody @Valid PacienteAlergiaDTO pacienteAlergiaDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteAlergiaService.createPacienteAlergia(pacienteAlergiaDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteAlergiaDTO> updatePacienteAlergia(@PathVariable Long id, @RequestBody @Valid PacienteAlergiaDTO pacienteAlergiaDTO) {
        return ResponseEntity.ok(pacienteAlergiaService.updatePacienteAlergia(id, pacienteAlergiaDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePacienteAlergia(@PathVariable Long id) {
        pacienteAlergiaService.deletePacienteAlergia(id);
        return ResponseEntity.noContent().build();
    }
}
