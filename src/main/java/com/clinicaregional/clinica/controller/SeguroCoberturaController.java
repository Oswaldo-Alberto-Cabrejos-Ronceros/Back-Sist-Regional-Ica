package com.clinicaregional.clinica.controller;


import com.clinicaregional.clinica.dto.SeguroCoberturaDTO;
import com.clinicaregional.clinica.service.SeguroCoberturaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seguro-coberturas")
public class SeguroCoberturaController {
    private final SeguroCoberturaService seguroCoberturaService;

    @Autowired
    public SeguroCoberturaController(SeguroCoberturaService seguroCoberturaService){
        this.seguroCoberturaService = seguroCoberturaService;
    }

    @GetMapping
    public ResponseEntity<List<SeguroCoberturaDTO>> listarSeguroCobertura(){
        return ResponseEntity.ok(seguroCoberturaService.listarSeguroCobertura());
    }

    @GetMapping("/seguro/{id}")
    public ResponseEntity<List<SeguroCoberturaDTO>> listarPorSeguro(@PathVariable Long id){
        return ResponseEntity.ok(seguroCoberturaService.listarPorSeguro(id));
    }

    @GetMapping("/cobertura/{id}")
    public ResponseEntity<List<SeguroCoberturaDTO>> listarPorCobertura(@PathVariable Long id){
        return ResponseEntity.ok(seguroCoberturaService.listarPorCobertura(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeguroCoberturaDTO> getSeguroCobertura(@PathVariable Long id){
        return seguroCoberturaService.getSeguroCoberturaById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SeguroCoberturaDTO> createSeguroCobertura(@RequestBody @Valid SeguroCoberturaDTO seguroCoberturaDTO){
        SeguroCoberturaDTO savedSeguroCoberturaDTO = seguroCoberturaService.createSeguroCobertura(seguroCoberturaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSeguroCoberturaDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeguroCobertura(@PathVariable Long id){
        seguroCoberturaService.deleteSeguroCobertura(id);
        return ResponseEntity.noContent().build();
    }
}
