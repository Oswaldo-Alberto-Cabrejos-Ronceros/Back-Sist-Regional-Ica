package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.AdministradorDTO;
import com.clinicaregional.clinica.dto.request.RegisterAdministradorRequest;
import com.clinicaregional.clinica.service.AdministradorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/administradores")
public class AdministradorController {
    private final AdministradorService administradorService;

    public AdministradorController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @GetMapping
    public ResponseEntity<List<AdministradorDTO>> listarAdministradores() {
        return ResponseEntity.ok(administradorService.listarAdministradores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministradorDTO> getAdministradorPorId(@PathVariable Long id) {
        return administradorService.getAdministradorById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AdministradorDTO> createAdministrador(@RequestBody @Valid RegisterAdministradorRequest registerAdministradorRequest) {
        AdministradorDTO savedAdministrador = administradorService.createAdministrador(registerAdministradorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdministrador);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdministradorDTO> updateAministrador(@PathVariable Long id, @RequestBody @Valid AdministradorDTO administradorDTO) {
        AdministradorDTO updatedAdministrador = administradorService.updateAdministrador(id, administradorDTO);
        return ResponseEntity.ok(updatedAdministrador);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AdministradorDTO> deleteAdministrador(@PathVariable Long id) {
        administradorService.deleteAdministrador(id);
        return ResponseEntity.noContent().build();
    }
}
