package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.service.CoberturaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.clinicaregional.clinica.dto.CoberturaDTO;
import java.util.List;

@RestController
@RequestMapping("/api/cobertura")
public class CoberturaController {
    private final CoberturaService coberturaService;

    @Autowired
    public CoberturaController(CoberturaService coberturaService) {
        this.coberturaService = coberturaService;
    }

    @GetMapping
    public ResponseEntity<List<CoberturaDTO>> listarCoberturas() {
        return ResponseEntity.ok(coberturaService.listarCoberturas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoberturaDTO> getCoberturaById(@PathVariable Long id) {
        return coberturaService.getCoberturaById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CoberturaDTO> createCobertura(@RequestBody @Valid CoberturaDTO coberturaDTO) {
        CoberturaDTO savedCobertura = coberturaService.createCobertura(coberturaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCobertura);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoberturaDTO> updateCobertura(@PathVariable Long id, @RequestBody @Valid CoberturaDTO coberturaDTO) {
        CoberturaDTO updatedCobertura = coberturaService.updateCobertura(id, coberturaDTO);
        return ResponseEntity.ok(updatedCobertura);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCobertura(@PathVariable Long id) {
        coberturaService.deleteCobertura(id);
        return ResponseEntity.noContent().build();
    }
}
