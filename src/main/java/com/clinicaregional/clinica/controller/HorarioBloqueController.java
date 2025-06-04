package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.request.HorarioBloqueRequest;
import com.clinicaregional.clinica.dto.response.HorarioBloqueResponse;
import com.clinicaregional.clinica.service.HorarioBloqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/horario-bloques")
@RequiredArgsConstructor
public class HorarioBloqueController {

    private final HorarioBloqueService horarioBloqueService;

    @GetMapping("/{id}")
    public ResponseEntity<HorarioBloqueResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(horarioBloqueService.obtenerPorId(id));
    }

    @GetMapping("/disponibilidad/{id}")
    public ResponseEntity<List<HorarioBloqueResponse>> listarPorDisponibilidad(
            @PathVariable("id") Long disponibilidadId) {
        return ResponseEntity.ok(horarioBloqueService.listarPorDisponibilidad(disponibilidadId));
    }

    @GetMapping("/medico/{id}")
    public ResponseEntity<List<HorarioBloqueResponse>> listarPorMedico(@PathVariable("id") Long medicoId) {
        return ResponseEntity.ok(horarioBloqueService.listarPorMedico(medicoId));
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<HorarioBloqueResponse>> listarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(horarioBloqueService.listarPorFecha(fecha));
    }

    @PostMapping
    public ResponseEntity<HorarioBloqueResponse> registrar(@Valid @RequestBody HorarioBloqueRequest request) {
        return ResponseEntity.ok(horarioBloqueService.registrar(request));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<HorarioBloqueResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestParam("estado") String nuevoEstado) {
        return ResponseEntity.ok(horarioBloqueService.actualizarEstado(id, nuevoEstado));
    }
}
