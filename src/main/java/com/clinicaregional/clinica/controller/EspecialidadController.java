package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.EspecialidadRequest;
import com.clinicaregional.clinica.dto.EspecialidadResponse;
import com.clinicaregional.clinica.service.EspecialidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    @Autowired
    public EspecialidadController(EspecialidadService especialidadService) {
        this.especialidadService = especialidadService;
    }

    @PostMapping
    public ResponseEntity<EspecialidadResponse> guardarEspecialidad(@RequestBody EspecialidadRequest especialidadRequest) {
        EspecialidadResponse response = especialidadService.guardarEspecialidad(especialidadRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadResponse> actualizarEspecialidad(@PathVariable Long id, @RequestBody EspecialidadRequest especialidadRequest) {
        EspecialidadResponse response = especialidadService.actualizarEspecialidad(id, especialidadRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEspecialidad(@PathVariable Long id) {
        especialidadService.eliminarEspecialidad(id);
        return ResponseEntity.noContent().build();
    }
}
