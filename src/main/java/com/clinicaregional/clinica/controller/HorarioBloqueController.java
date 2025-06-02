package com.clinicaregional.clinica.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinicaregional.clinica.dto.request.HorarioBloqueRequest;
import com.clinicaregional.clinica.dto.response.HorarioBloqueResponse;
import com.clinicaregional.clinica.enums.EstadoBloque;
import com.clinicaregional.clinica.service.HorarioBloqueService;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class HorarioBloqueController {
    private final HorarioBloqueService horarioBloqueService;

    @Autowired
    public HorarioBloqueController(HorarioBloqueService horarioBloqueService) {
        this.horarioBloqueService = horarioBloqueService;
    }   

    
    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<HorarioBloqueResponse>> obtenerPorMedicoId(@PathVariable Long medicoId) {
        return ResponseEntity.ok(horarioBloqueService.obtenerHoraiosBloquesPorMedicoId(medicoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioBloqueResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(horarioBloqueService.obtenerHorarioBloquePorId(id));
    }

    @GetMapping("/{id}/disponible")
    public ResponseEntity<Boolean> estaDisponible(@PathVariable Long id) {
        return ResponseEntity.ok(horarioBloqueService.estaDiponibleHorarioBloque(id));
    }

    @PostMapping
    public ResponseEntity<HorarioBloqueResponse> crear(@RequestBody HorarioBloqueRequest request) {
        return ResponseEntity.ok(horarioBloqueService.crearHorarioBloque(request));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<HorarioBloqueResponse> actualizarEstado(@PathVariable Long id, @RequestParam EstadoBloque estado) {
        return ResponseEntity.ok(horarioBloqueService.actualizarEstadoHorarioBloque(id, estado));
    }

    @PutMapping("/{id}/liberar")
    public ResponseEntity<Void> liberar(@PathVariable Long id) {
        horarioBloqueService.liberarHorarioBloque(id);
        return ResponseEntity.noContent().build();
    }
}

