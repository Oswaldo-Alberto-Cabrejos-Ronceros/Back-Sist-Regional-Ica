package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.request.EspecialidadRequest;
import com.clinicaregional.clinica.dto.response.EspecialidadResponse;
import com.clinicaregional.clinica.service.EspecialidadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    @Autowired
    public EspecialidadController(EspecialidadService especialidadService) {
        this.especialidadService = especialidadService;
    }

    @GetMapping
    public ResponseEntity<List<EspecialidadResponse>> listarEspecialidades() {
        List<EspecialidadResponse> especialidades = especialidadService.listarEspecialidades();
        return ResponseEntity.ok(especialidades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadResponse> obtenerEspecialidadPorId(@PathVariable Long id) {
        return especialidadService.getEspecialidadById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EspecialidadResponse> crearEspecialidad(@RequestBody @Valid EspecialidadRequest especialidadRequest) {
        EspecialidadResponse response = especialidadService.guardarEspecialidad(especialidadRequest);
        return ResponseEntity.status(201).body(response); // 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEspecialidad(@PathVariable Long id, @RequestBody @Valid EspecialidadRequest especialidadRequest) {
        try {
            EspecialidadResponse response = especialidadService.actualizarEspecialidad(id, especialidadRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("no encontrada")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEspecialidad(@PathVariable Long id) {
        try {
            especialidadService.eliminarEspecialidad(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("no encontrada")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
