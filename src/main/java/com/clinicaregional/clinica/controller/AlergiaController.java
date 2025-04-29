package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.AlergiaDTO;
import com.clinicaregional.clinica.enums.TipoAlergia;
import com.clinicaregional.clinica.service.AlergiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/alergias")
public class AlergiaController {
    private final AlergiaService alergiaService;

    @Autowired
    public AlergiaController(AlergiaService alergiaService) {
        this.alergiaService = alergiaService;
    }

    @GetMapping
    public ResponseEntity<List<AlergiaDTO>> listarAlergias() {
        return ResponseEntity.ok(alergiaService.listarAlergias());
    }

    @GetMapping("/{tipoAlergia}")
    public ResponseEntity<List<AlergiaDTO>> listarAlergiaPorId(@PathVariable TipoAlergia tipoAlergia) {
        return ResponseEntity.ok(alergiaService.listarAlergiasPorTipo(tipoAlergia));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<AlergiaDTO> getAlergiaById(@PathVariable Long id) {
        return alergiaService.getAlergiaPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AlergiaDTO> createAlergia(@RequestBody @Valid AlergiaDTO alergiaDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alergiaService.crearAlergia(alergiaDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlergiaDTO> updateAlergia(@PathVariable Long id, @RequestBody @Valid AlergiaDTO alergiaDTO) {
        return ResponseEntity.ok(alergiaService.updateAlergia(id, alergiaDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlergia(@PathVariable Long id) {
        alergiaService.eliminarAlergia(id);
        return ResponseEntity.noContent().build();
    }
}
