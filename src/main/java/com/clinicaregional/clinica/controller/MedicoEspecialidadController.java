package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.request.MedicoEspecialidadRequest;
import com.clinicaregional.clinica.dto.response.MedicoEspecialidadResponse;
import com.clinicaregional.clinica.service.MedicoEspecialidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medico-especialidad")
@RequiredArgsConstructor
public class MedicoEspecialidadController {

    private final MedicoEspecialidadService medicoEspecialidadService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicoEspecialidadResponse registrarRelacionME(@RequestBody MedicoEspecialidadRequest request) {
        return medicoEspecialidadService.registrarRelacionME(request);
    }

    @PutMapping("/{medicoId}/{especialidadId}")
    public MedicoEspecialidadResponse actualizarRelacionME(@PathVariable Long medicoId,
            @PathVariable Long especialidadId,
            @RequestBody MedicoEspecialidadRequest request) {
        return medicoEspecialidadService.actualizarRelacionME(medicoId, especialidadId, request);
    }

    @DeleteMapping("/{medicoId}/{especialidadId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarRelacionME(@PathVariable Long medicoId, @PathVariable Long especialidadId) {
        medicoEspecialidadService.eliminarRelacionME(medicoId, especialidadId);
    }

    @GetMapping("/medico/{medicoId}")
    public List<MedicoEspecialidadResponse> obtenerEspecialidadDelMedico(@PathVariable Long medicoId) {
        return medicoEspecialidadService.obtenerEspecialidadDelMedico(medicoId);
    }

    
    @GetMapping("/especialidad/{especialidadId}")
    public List<MedicoEspecialidadResponse> obtenerMedicosPorEspecialidad(@PathVariable Long especialidadId) {
        return medicoEspecialidadService.obtenerMedicosPorEspecialidad(especialidadId);
    }
}
