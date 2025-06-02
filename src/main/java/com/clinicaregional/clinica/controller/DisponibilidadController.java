package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.request.DisponibilidadRequest;
import com.clinicaregional.clinica.dto.response.DisponibilidadResponse;
import com.clinicaregional.clinica.service.DisponibilidadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disponibilidad")
public class DisponibilidadController {
    private final DisponibilidadService disponibilidadService;

    public DisponibilidadController(DisponibilidadService disponibilidadService) {
        this.disponibilidadService = disponibilidadService;
    }

    @PostMapping
    public ResponseEntity<DisponibilidadResponse> registrar(@RequestBody @Valid DisponibilidadRequest disponibilidadRequest) {
        DisponibilidadResponse create = disponibilidadService.registrar(disponibilidadRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(create);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadResponse> getDisponibilidadById(@PathVariable Long id) {
        return ResponseEntity.ok(disponibilidadService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<DisponibilidadResponse>> listar() {
        return ResponseEntity.ok(disponibilidadService.listar());
    }

    @GetMapping("/medico/{id}")
    public ResponseEntity<List<DisponibilidadResponse>> listarPorMedicoId(@PathVariable Long id) {
        return ResponseEntity.ok(disponibilidadService.listarPorMedicoId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadResponse> actualizar(@PathVariable Long id, @RequestBody @Valid DisponibilidadRequest disponibilidadRequest) {
        return ResponseEntity.ok(disponibilidadService.actualizar(id, disponibilidadRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        disponibilidadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
