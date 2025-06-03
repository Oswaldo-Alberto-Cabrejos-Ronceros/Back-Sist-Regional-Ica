package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
import com.clinicaregional.clinica.entity.Cita;
import com.clinicaregional.clinica.entity.HorarioBloque;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.entity.Servicio;
import com.clinicaregional.clinica.enums.EstadoBloque;
import com.clinicaregional.clinica.enums.EstadoCita;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.CitaMapper;
import com.clinicaregional.clinica.repository.CitaRepository;
import com.clinicaregional.clinica.repository.HorarioBloqueRepository;
import com.clinicaregional.clinica.repository.MedicoRepository;
import com.clinicaregional.clinica.repository.PacienteRepository;
import com.clinicaregional.clinica.repository.ServicioRepository;
import com.clinicaregional.clinica.service.CitaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;
    private final HorarioBloqueRepository horarioBloqueRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final ServicioRepository servicioRepository;

    @Transactional
    @Override
    public CitaResponse registrar(CitaRequest request) {
        // 1. Buscar bloque horario DISPONIBLE
        HorarioBloque bloque = horarioBloqueRepository
                .findByFechaAndHoraInicioAndEstadoBloque(
                        request.getFecha(),
                        request.getHora(),
                        EstadoBloque.DISPONIBLE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No hay bloques DISPONIBLES para la fecha " + request.getFecha() +
                                " y hora " + request.getHora()));

        // 2. Verificar que el bloque no esté ocupado
        if (bloque.getCita() != null) {
            throw new IllegalStateException("Ese bloque horario ya tiene una cita asignada");
        }

        // 3. Validar existencia de entidades necesarias
        Long pacienteId = request.getPacienteId();
        Long medicoId = request.getMedicoId();
        Long servicioId = request.getServicioId();

        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico  no encontrado"));
        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        // 4. Crear entidad Cita y asignar relaciones
        Cita cita = citaMapper.toEntity(request);
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setServicio(servicio);
        cita.setEstadoCita(EstadoCita.PENDIENTE);

        // 5. Guardar la cita
        Cita citaGuardada = citaRepository.save(cita);

        // 6. Enlazar cita al bloque
        bloque.setCita(citaGuardada);
        bloque.setEstadoBloque(EstadoBloque.OCUPADO);
        horarioBloqueRepository.save(bloque);

        // 7. Devolver respuesta
        return citaMapper.toResponse(citaGuardada);
    }

    @Override
    public CitaResponse obtenerPorId(Long id) {
        return citaRepository.findById(id)
                .map(citaMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));
    }

    @Override
    public List<CitaResponse> listarTodas() {
        return citaRepository.findAll()
                .stream()
                .map(citaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CitaResponse actualizar(Long id, CitaRequest request) {
        return citaRepository.findById(id)
                .map(cita -> {
                    cita.setFecha(request.getFecha());
                    cita.setHora(request.getHora());
                    cita.setNotas(request.getNotas());
                    cita.setAntecedentes(request.getAntecedentes());
                    return citaMapper.toResponse(citaRepository.save(cita));
                })
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));
    }

    @Transactional
    @Override
    public void eliminar(Long id) {
        if (!citaRepository.existsById(id)) {
            throw new RuntimeException("Cita no encontrada con ID: " + id);
        }
        citaRepository.deleteById(id);
    }
}
