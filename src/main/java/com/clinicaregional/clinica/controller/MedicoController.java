package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.request.MedicoRequestDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponseDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponsePublicDTO;
import com.clinicaregional.clinica.dto.response.MyInfoMedico;
import com.clinicaregional.clinica.service.MedicoService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final MedicoService medicoService;

    @Autowired
    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @GetMapping
    public ResponseEntity<List<MedicoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(medicoService.obtenerMedicos());
    }

    //para obtener datos publicos de los medicos
    @GetMapping("/public")
    public ResponseEntity<List<MedicoResponsePublicDTO>> obtenerTodosPublico() {
        return ResponseEntity.ok(medicoService.obtenerMedicosPublic());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> obtenerMedicoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medicoService.obtenerMedicoPorId(id));
    }

    //para obtener myInfo
    @GetMapping("/my-info/{id}")
    public ResponseEntity<MyInfoMedico> obtenerMyInfo(@PathVariable Long id) {
        return ResponseEntity.ok(medicoService.obtenerMyInfoMedico(id));
    }

    @PostMapping
    public ResponseEntity<MedicoResponseDTO> crear(@RequestPart("dto") @Valid MedicoRequestDTO dto, @RequestPart(value = "imagen", required = false) MultipartFile imagen) {
        MedicoResponseDTO creado = medicoService.guardarMedico(dto, imagen);
        return ResponseEntity.status(201).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> actualizar(@PathVariable Long id,
                                                        @RequestPart("dto") @Valid MedicoRequestDTO dto, @RequestPart(value = "imagen", required = false) MultipartFile imagen) {
        MedicoResponseDTO actualizado = medicoService.actualizarMedico(id, dto, imagen);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        medicoService.eliminarMedico(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-user/{usuarioId}")
    public ResponseEntity<MedicoResponseDTO> obtenerPorUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(medicoService.obtenerMedicoPorUsuarioId(usuarioId));
    }

}
