package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.dto.request.OpinionRequest;
import com.clinicaregional.clinica.dto.response.OpinionResponse;
import com.clinicaregional.clinica.service.OpinionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/opiniones")
@RequiredArgsConstructor
public class OpinionController {

    private final OpinionService opinionService;

    //Obtener todas las opiniones visibles
    @GetMapping
    public List<OpinionResponse> listar() {
        return opinionService.listar();
    }

    //Obtener una opinión por ID
    @GetMapping("/{id}")
    public Optional<OpinionResponse> obtenerPorId(@PathVariable Long id) {
        return opinionService.obtenerPorId(id);
    }

    //Crear una nueva opinión
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OpinionResponse guardar(@RequestBody OpinionRequest opinionRequest) {
        return opinionService.guardar(opinionRequest);
    }

    //Eliminar (ocultar) una opinión por ID
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        opinionService.eliminar(id);
    }
}
