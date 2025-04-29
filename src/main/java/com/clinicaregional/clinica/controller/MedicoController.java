package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.request.MedicoRequestDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponseDTO;
import com.clinicaregional.clinica.service.MedicoService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final MedicoService medicoService;

    @Autowired
    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @GetMapping
    public ResponseEntity<List<MedicoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(medicoService.obtenerMedicos());
    }

    @PostMapping
    public ResponseEntity<MedicoResponseDTO> crear(@RequestBody @Valid MedicoRequestDTO dto) {
        MedicoResponseDTO creado = medicoService.guardarMedico(dto);
        return ResponseEntity.status(201).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> actualizar(@PathVariable Long id,
            @RequestBody @Valid MedicoRequestDTO dto) {
        try {
            MedicoResponseDTO actualizado = medicoService.actualizarMedico(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        medicoService.eliminarMedico(id);
        return ResponseEntity.noContent().build();
    }
}
