package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.response.PagedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.clinicaregional.clinica.service.ServicioService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.clinicaregional.clinica.dto.request.ServicioRequest;
import com.clinicaregional.clinica.dto.response.ServicioResponse;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    private final ServicioService servicioService;

    @Autowired
    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping
    public ResponseEntity<List<ServicioResponse>> obtenerServicios() {
        return ResponseEntity.ok(servicioService.obtenerServicios());
    }

    @GetMapping("/especialidad/{id}")
    public ResponseEntity<List<ServicioResponse>> obtenerServiciosByEspecialidad(@PathVariable Long id) {
        return ResponseEntity.ok(servicioService.obtenerServiciosPorEspecialidadId(id));
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

    @GetMapping("/paginado")
    public ResponseEntity<PagedResponse<ServicioResponse>> listarServiciosPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(servicioService.obtenerServiciosPaginado(pageable));
    }

}
