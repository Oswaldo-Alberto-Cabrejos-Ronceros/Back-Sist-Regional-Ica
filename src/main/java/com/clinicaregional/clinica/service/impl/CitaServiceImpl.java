package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
import com.clinicaregional.clinica.entity.Cita;
import com.clinicaregional.clinica.repository.CitaRepository;
import com.clinicaregional.clinica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Override
    public List<CitaResponse> listar() {
        List<Cita> citas = citaRepository.findAll();
        return citas.stream()
                .map(cita -> new CitaResponse(cita.getId(), cita.getFecha(), cita.getHora(), cita.isEstadoCita(), cita.getNotas()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CitaResponse> obtenerPorId(Long id) {
        Optional<Cita> cita = citaRepository.findById(id);
        return cita.map(c -> new CitaResponse(c.getId(), c.getFecha(), c.getHora(), c.isEstadoCita(), c.getNotas()));
    }

    @Override
    public CitaResponse guardar(CitaRequest citaRequest) {
        Cita cita = new Cita();
        // Conversión de java.util.Date a java.sql.Date
        cita.setFecha(new java.sql.Date(citaRequest.getFecha().getTime()));
        cita.setHora(citaRequest.getHora());
        cita.setEstadoCita(citaRequest.isEstadoCita());
        cita.setNotas(citaRequest.getNotas());
        cita.setAntecedentes(citaRequest.getAntecedentes());
        Cita savedCita = citaRepository.save(cita);
        return new CitaResponse(savedCita.getId(), savedCita.getFecha(), savedCita.getHora(), savedCita.isEstadoCita(), savedCita.getNotas());
    }

    @Override
    public CitaResponse actualizar(Long id, CitaRequest citaRequest) {
        Optional<Cita> existingCita = citaRepository.findById(id);
        if (existingCita.isPresent()) {
            Cita cita = existingCita.get();
            //Conversión de java.util.Date a java.sql.Date
            cita.setFecha(new java.sql.Date(citaRequest.getFecha().getTime()));
            cita.setHora(citaRequest.getHora());
            cita.setEstadoCita(citaRequest.isEstadoCita());
            cita.setNotas(citaRequest.getNotas());
            cita.setAntecedentes(citaRequest.getAntecedentes());
            Cita updatedCita = citaRepository.save(cita);
            return new CitaResponse(updatedCita.getId(), updatedCita.getFecha(), updatedCita.getHora(), updatedCita.isEstadoCita(), updatedCita.getNotas());
        } else {
            throw new RuntimeException("Cita no encontrada");
        }
    }

    @Override
    public void eliminar(Long id) {
        citaRepository.deleteById(id);
    }
}
