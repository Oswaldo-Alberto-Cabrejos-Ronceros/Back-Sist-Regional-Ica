package com.clinicaregional.clinica.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.clinicaregional.clinica.service.ServicioService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.clinicaregional.clinica.dto.request.ServicioRequest;
import com.clinicaregional.clinica.dto.response.ServicioResponse;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    private final ServicioService servicioService;

    @Autowired
    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @PostMapping
    public ResponseEntity<ServicioResponse> agregarServicio(@RequestBody @Valid ServicioRequest servicioRequest) {
        ServicioResponse servicioResponse = servicioService.agregarServicio(servicioRequest);
        return ResponseEntity.ok(servicioResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarServicio(@PathVariable Long id,
            @RequestBody @Valid ServicioRequest servicioRequest) {
        try {
            ServicioResponse servicioResponse = servicioService.actualizarServicio(id, servicioRequest);
            return ResponseEntity.ok(servicioResponse);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("no encontrada")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());

        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarServicio(@PathVariable Long id) {
        try {
            servicioService.eliminarServicio(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
