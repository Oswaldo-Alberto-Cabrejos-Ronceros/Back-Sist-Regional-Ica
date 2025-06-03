package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
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
        return ResponseEntity.ok(citaService.registrar(request));
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
}
