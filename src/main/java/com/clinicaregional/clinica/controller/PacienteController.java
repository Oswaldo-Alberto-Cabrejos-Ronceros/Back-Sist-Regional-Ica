package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.PacienteConUserDTO;
import com.clinicaregional.clinica.dto.PacienteSinUserDTO;
import com.clinicaregional.clinica.dto.request.UpdatePacienteDTO;
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
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<List<PacienteConUserDTO>> listarPacientes() {
        return ResponseEntity.ok(pacienteService.listarPacientes());
    }

    // Listar pacientes sin importar el filtro de estado
    @GetMapping("/estado")
    public ResponseEntity<List<PacienteConUserDTO>> listarPacientesPorEstado() {
        return ResponseEntity.ok(pacienteService.listarPacientesPorEstado());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PacienteConUserDTO> getPacienteById(@PathVariable Long id) {
        return pacienteService.getPacientePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/num-identificacion/{numIdentificacion}")
    public ResponseEntity<PacienteConUserDTO> getPacienteByNumIdentificacion(@PathVariable String numIdentificacion) {
        return pacienteService.getPacientePorIdentificacion(numIdentificacion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/my-info/{id}")
    public ResponseEntity<MyInfoPaciente> getMyInfoPaciente(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.getMyInfoPaciente(id));
    }

    // Crear paciente sin usuario
    @PostMapping("/datosIniciales")
    public ResponseEntity<PacienteSinUserDTO> createPacienteSimple(@RequestBody @Valid PacienteSinUserDTO pacienteDTO) {
        PacienteSinUserDTO savedPaciente = pacienteService.crearPacienteSimple(pacienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPaciente);
    }

    // @PostMapping
    // public ResponseEntity<PacienteConUserDTO> createPaciente(@RequestBody @Valid PacienteConUserDTO pacienteDTO) {
    //     PacienteConUserDTO savedPaciente = pacienteService.crearPacientePorWeb(pacienteDTO);
    //     return ResponseEntity.status(HttpStatus.CREATED).body(savedPaciente);
    // }UpdatePacienteDTO

    @PutMapping("/{id}")
    public ResponseEntity<PacienteConUserDTO> updatePaciente(@PathVariable Long id,
                                                             @RequestPart("pacienteDTO") @Valid UpdatePacienteDTO updatePacienteDTO,@RequestPart(value = "imagen", required = false) MultipartFile imagen) {

        PacienteConUserDTO updatedPaciente = pacienteService.actualizarPaciente(id, updatePacienteDTO,imagen);
        return ResponseEntity.ok(updatedPaciente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaciente(@PathVariable Long id) {
        pacienteService.eliminarPaciente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paginado")
    public ResponseEntity<PagedResponse<PacienteConUserDTO>> listarPacientesPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(pacienteService.listarPacientesPaginado(pageable));
    }

}