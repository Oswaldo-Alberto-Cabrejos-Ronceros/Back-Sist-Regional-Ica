package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.entity.Recepcionista;
import com.clinicaregional.clinica.service.RecepcionistaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<Recepcionista>> listarTodos() {
        return ResponseEntity.ok(recepcionistaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recepcionista> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(recepcionistaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Recepcionista> registrar(@RequestBody Recepcionista recepcionista) {
        return ResponseEntity.ok(recepcionistaService.guardar(recepcionista));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recepcionista> actualizar(@PathVariable Long id, @RequestBody Recepcionista recepcionista) {
        return ResponseEntity.ok(recepcionistaService.actualizar(id, recepcionista));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recepcionistaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
