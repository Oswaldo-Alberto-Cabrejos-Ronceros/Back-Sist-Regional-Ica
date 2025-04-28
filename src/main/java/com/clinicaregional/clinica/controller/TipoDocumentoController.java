package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.TipoDocumentoDTO;
import com.clinicaregional.clinica.service.TipoDocumentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/tipos-documentos")
public class TipoDocumentoController {

    private final TipoDocumentoService tipoDocumentoService;

    @Autowired
    public TipoDocumentoController(TipoDocumentoService tipoDocumentoService) {
        this.tipoDocumentoService = tipoDocumentoService;
    }

    @GetMapping
    public ResponseEntity<List<TipoDocumentoDTO>> getTipoDocumento() {
        return ResponseEntity.ok(tipoDocumentoService.listarTipoDocumento());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoDocumentoDTO> getTipoDocumentoById(@PathVariable Long id) {
        return tipoDocumentoService.getTipoDocumentoById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TipoDocumentoDTO> createTipoDocumento(@RequestBody @Valid TipoDocumentoDTO tipoDocumentoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoDocumentoService.createTipoDocumento(tipoDocumentoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoDocumentoDTO> updateTipoDocumento(@PathVariable Long id, @RequestBody @Valid TipoDocumentoDTO tipoDocumentoDTO) {
        TipoDocumentoDTO actualizado = tipoDocumentoService.updateTipoDocumento(id, tipoDocumentoDTO);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteTipoDocumento(@PathVariable Long id) {
        tipoDocumentoService.deleteTipoDocumento(id);
        return ResponseEntity.noContent().build();
    }
}
