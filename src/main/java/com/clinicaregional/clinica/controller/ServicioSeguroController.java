package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.ServicioSeguroDTO;
import com.clinicaregional.clinica.service.ServicioSeguroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios-seguros")
public class ServicioSeguroController {
    private final ServicioSeguroService servicioSeguroService;

    @Autowired
    public ServicioSeguroController(ServicioSeguroService servicioSeguroService) {
        this.servicioSeguroService = servicioSeguroService;
    }

    @GetMapping
    public ResponseEntity<List<ServicioSeguroDTO>> listarServicioSeguro() {
        return ResponseEntity.ok(servicioSeguroService.listarServicioSeguro());
    }

    @GetMapping("/servicio/{id}")
    public ResponseEntity<List<ServicioSeguroDTO>> listarPorServicio(@PathVariable Long id) {
        return ResponseEntity.ok(servicioSeguroService.listarPorServicio(id));
    }

    @GetMapping("/seguro/{id}")
    public ResponseEntity<List<ServicioSeguroDTO>> listarPorSeguro(@PathVariable Long id) {
        return ResponseEntity.ok(servicioSeguroService.listarPorSeguro(id));
    }

    @GetMapping("/cobertura/{id}")
    public ResponseEntity<List<ServicioSeguroDTO>> listarPorCobertura(@PathVariable Long id) {
        return ResponseEntity.ok(servicioSeguroService.listarPorCobertura(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioSeguroDTO> getSeguroServicioById(@PathVariable Long id) {
        return servicioSeguroService.getSeguroServicioById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ServicioSeguroDTO> createSeguroService(@RequestBody @Valid ServicioSeguroDTO servicioSeguroDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioSeguroService.createServicioSeguro(servicioSeguroDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeguroService(@PathVariable Long id) {
        servicioSeguroService.deleteServicioSeguro(id);
        return ResponseEntity.noContent().build();
    }

}
