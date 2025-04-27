package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.request.RecepcionistaRequest;
import com.clinicaregional.clinica.dto.response.RecepcionistaResponse;
import com.clinicaregional.clinica.entity.Recepcionista;
import com.clinicaregional.clinica.service.RecepcionistaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recepcionistas")
@RequiredArgsConstructor
@Tag(name = "Recepcionista", description = "Operaciones CRUD para recepcionistas")
public class RecepcionistaController {
    private final RecepcionistaService recepcionistaService;
    @GetMapping
    public ResponseEntity<List<RecepcionistaResponse>> listarTodos() {
        return ResponseEntity.ok(recepcionistaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecepcionistaResponse> obtenerPorId(@PathVariable Long id) {
        return recepcionistaService.obtenerPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RecepcionistaResponse> registrar(@RequestBody RecepcionistaRequest recepcionista) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recepcionistaService.guardar(recepcionista));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecepcionistaResponse> actualizar(@PathVariable Long id, @RequestBody RecepcionistaRequest recepcionista) {
        return ResponseEntity.ok(recepcionistaService.actualizar(id, recepcionista));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recepcionistaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
