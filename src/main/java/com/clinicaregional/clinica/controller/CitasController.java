package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.request.CitaReprogramarRequest;
import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
import com.clinicaregional.clinica.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/citas")
public class CitasController {

    private CitaService citaService;

    @Autowired
    public CitasController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    public ResponseEntity<List<CitaResponse>> listarCitas() {
        List<CitaResponse> citas = citaService.listar();
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaResponse> getCitaById(@PathVariable Long id) {
        return citaService.obtenerPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CitaResponse> createCita(@RequestBody @Valid CitaRequest citaRequest) {
        CitaResponse savedCita = citaService.guardar(citaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCita);
    }

    @PutMapping("{id}")
    public ResponseEntity<CitaResponse> updateCita(@PathVariable Long id, @RequestBody @Valid CitaRequest citaRequest) {
        CitaResponse updatedCita = citaService.guardar(citaRequest);
        return ResponseEntity.ok(updatedCita);
    }

    @PatchMapping("/cancelar/{id}")
    public ResponseEntity<?> cancelarCita(@PathVariable Long id){
        citaService.cancelar(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/reprogramar/{id}")
    public ResponseEntity<CitaResponse> reprogramarCita(@PathVariable Long id, @RequestBody @Valid CitaReprogramarRequest citaReprogramarRequest){
      CitaResponse cita= citaService.reprogramar(id, citaReprogramarRequest);
      return ResponseEntity.ok(cita);
    }
    @PatchMapping("/atendida/{id}")
    public ResponseEntity<?> marcarAtendida(@PathVariable Long id){
        citaService.marcarAtentida(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/no-asistio/{id}")
    public ResponseEntity<?> marcarNoAsistio(@PathVariable Long id){
        citaService.marcoNoAsistio(id);
        return ResponseEntity.ok().build();
    }
}

