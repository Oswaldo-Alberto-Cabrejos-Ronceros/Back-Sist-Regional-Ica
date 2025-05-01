package com.clinicaregional.clinica.controller;


import com.clinicaregional.clinica.dto.SeguroDTO;
import com.clinicaregional.clinica.enums.EstadoSeguro;
import com.clinicaregional.clinica.service.SeguroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seguro")
public class SeguroController {
    private final SeguroService seguroService;

    public SeguroController(SeguroService seguroService) {
        this.seguroService = seguroService;
    }

    @GetMapping
    public ResponseEntity<List<SeguroDTO>> listarSeguros() {
        return ResponseEntity.ok(seguroService.listarSeguros());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<SeguroDTO> getSeguroById(@PathVariable Long id) {
        return seguroService.getSeguroById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<SeguroDTO> getSeguroByNombre(@PathVariable String nombre) {
        return seguroService.getSeguroByNombre(nombre).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SeguroDTO> createSeguro(@RequestBody @Valid SeguroDTO seguroDTO) {
        SeguroDTO savedSeguro = seguroService.createSeguro(seguroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSeguro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeguroDTO> updateSeguro(@PathVariable Long id, @RequestBody @Valid SeguroDTO seguroDTO) {
        SeguroDTO updatedSeguro = seguroService.updateSeguro(id, seguroDTO);
        return ResponseEntity.ok(updatedSeguro);
    }

    @PatchMapping("/estado-seguro/{id}")
    public ResponseEntity<SeguroDTO> updateEstadoSeguro(@PathVariable Long id, @RequestBody EstadoSeguro estadoSeguro) {
        SeguroDTO updateSeguro = seguroService.updateEstadoSeguro(id, estadoSeguro);
        return ResponseEntity.ok(updateSeguro);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeguro(@PathVariable Long id) {
        seguroService.deleteSeguro(id);
        return ResponseEntity.noContent().build();
    }
}
