package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.request.RecepcionistaRequest;
import com.clinicaregional.clinica.dto.response.MyInfoRecepcionista;
import com.clinicaregional.clinica.dto.response.RecepcionistaResponse;
import com.clinicaregional.clinica.service.RecepcionistaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/recepcionistas")
@RequiredArgsConstructor
@Tag(name = "Recepcionista", description = "Operaciones CRUD para recepcionistas")
public class RecepcionistaController {

    private final RecepcionistaService recepcionistaService;

    @PostMapping
    public ResponseEntity<?> registrar(@RequestPart("recepcionistaRequest") @Valid RecepcionistaRequest recepcionistaRequest,
                                       BindingResult result,@RequestPart(value = "imagen", required = false) MultipartFile imagen) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Datos inválidos");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(recepcionistaService.guardar(recepcionistaRequest,imagen));
    }

    @GetMapping
    public ResponseEntity<List<RecepcionistaResponse>> listar() {
        return ResponseEntity.ok(recepcionistaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecepcionistaResponse> obtenerPorId(@PathVariable Long id) {
        return recepcionistaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/my-info/{id}")
    public ResponseEntity<MyInfoRecepcionista> obtenerMyInfoRecepcionista(@PathVariable Long id) {
        MyInfoRecepcionista myInfoRecepcionista = recepcionistaService.obtenerMyInfoRecepcionista(id);
        return ResponseEntity.ok(myInfoRecepcionista);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecepcionistaResponse> actualizar(@PathVariable Long id,
                                                            @RequestPart("recepcionistaRequest") @Valid RecepcionistaRequest recepcionistaRequest,
                                                            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {
        return ResponseEntity.ok(recepcionistaService.actualizar(id, recepcionistaRequest,imagen));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recepcionistaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}