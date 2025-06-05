package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.PacienteDTO;
import com.clinicaregional.clinica.dto.response.MyInfoPaciente;
import com.clinicaregional.clinica.dto.response.PagedResponse;
import com.clinicaregional.clinica.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("api/pacientes")
public class PacienteController {
    private final PacienteService pacienteService;

    @Autowired
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public ResponseEntity<List<PacienteDTO>> listarPacientes() {
        return ResponseEntity.ok(pacienteService.listarPacientes());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PacienteDTO> getPacienteById(@PathVariable Long id) {
        return pacienteService.getPacientePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/num-identificacion/{numIdentificacion}")
    public ResponseEntity<PacienteDTO> getPacienteByNumIdentificacion(@PathVariable String numIdentificacion) {
        return pacienteService.getPacientePorIdentificacion(numIdentificacion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/my-info/{id}")
    public ResponseEntity<MyInfoPaciente> getMyInfoPaciente(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.getMyInfoPaciente(id));
    }

    @PostMapping
    public ResponseEntity<PacienteDTO> createPaciente(@RequestBody @Valid PacienteDTO pacienteDTO) {
        PacienteDTO savedPaciente = pacienteService.crearPaciente(pacienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPaciente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteDTO> updatePaciente(@PathVariable Long id,
                                                      @RequestBody @Valid PacienteDTO pacienteDTO) {
        PacienteDTO updatedPaciente = pacienteService.actualizarPaciente(id, pacienteDTO);
        return ResponseEntity.ok(updatedPaciente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaciente(@PathVariable Long id) {
        pacienteService.eliminarPaciente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paginado")
    public ResponseEntity<PagedResponse<PacienteDTO>> listarPacientesPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(pacienteService.listarPacientesPaginado(pageable));
    }

}
