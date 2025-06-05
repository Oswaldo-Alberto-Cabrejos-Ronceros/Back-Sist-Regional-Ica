package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
import com.clinicaregional.clinica.dto.response.PacienteResponseDTO;
import com.clinicaregional.clinica.entity.Cita;
import com.clinicaregional.clinica.entity.HorarioBloque;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.entity.Servicio;
import com.clinicaregional.clinica.enums.EstadoBloque;
import com.clinicaregional.clinica.enums.EstadoCita;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.CitaMapper;
import com.clinicaregional.clinica.mapper.PacienteMapper;
import com.clinicaregional.clinica.repository.CitaRepository;
import com.clinicaregional.clinica.repository.HorarioBloqueRepository;
import com.clinicaregional.clinica.repository.MedicoRepository;
import com.clinicaregional.clinica.repository.PacienteRepository;
import com.clinicaregional.clinica.repository.ServicioRepository;
import com.clinicaregional.clinica.service.CitaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitaServiceImpl implements CitaService {

    private final PacienteMapper pacienteMapper;

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

        // Verificación crítica: asegurar que el bloque pertenece al médico esperado
        if (!bloque.getDisponibilidad().getMedico().getId().equals(request.getMedicoId())) {
            throw new IllegalArgumentException("El bloque horario no pertenece al médico especificado.");
        }

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
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado"));
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
    public CitaResponse confirmarCita(Long citaId) {
        // 1. Buscar la cita
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + citaId));

        // 2. Validar estado actual de la cita
        if (!(cita.getEstadoCita() == EstadoCita.PENDIENTE || cita.getEstadoCita() == EstadoCita.REPROGRAMADA)) {
            throw new IllegalStateException("Solo se pueden confirmar citas en estado PENDIENTE");
        }

        // 3. Validar que relaciones estén presentes
        if (cita.getPaciente() == null || cita.getMedico() == null || cita.getServicio() == null) {
            throw new IllegalStateException("La cita tiene datos incompletos (paciente, médico o servicio faltantes)");
        }

        // 4. Validar que esté asociada a un bloque horario
        HorarioBloque bloque = horarioBloqueRepository.findByCitaId(cita.getId())
                .orElseThrow(() -> new IllegalStateException("La cita no está asociada a un bloque horario"));

        // 5. Validar que el bloque esté en estado OCUPADO
        if (bloque.getEstadoBloque() != EstadoBloque.OCUPADO) {
            throw new IllegalStateException("El bloque asociado debe estar en estado OCUPADO para confirmar la cita");
        }

        // 6. Confirmar la cita
        cita.setEstadoCita(EstadoCita.CONFIRMADA);
        Cita citaActualizada = citaRepository.save(cita);

        return citaMapper.toResponse(citaActualizada);
    }

    @Transactional
    @Override
    public void eliminar(Long id) {
        if (!citaRepository.existsById(id)) {
            throw new RuntimeException("Cita no encontrada con ID: " + id);
        }
        citaRepository.deleteById(id);
    }

    @Transactional
    @Override
    public CitaResponse cancelarCita(Long citaId) {
        // 1. Buscar la cita
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + citaId));

        // 2. Validar que esté en estado PENDIENTE o REPROGRAMADA
        if (!(cita.getEstadoCita() == EstadoCita.PENDIENTE || cita.getEstadoCita() == EstadoCita.REPROGRAMADA)) {
            throw new IllegalStateException("Solo se pueden cancelar citas en estado PENDIENTE");
        }

        // 3. Obtener el bloque horario asociado
        HorarioBloque bloque = horarioBloqueRepository.findByCitaId(citaId)
                .orElseThrow(() -> new IllegalStateException("La cita no está asociada a un bloque horario"));

        // 4. Cambiar estado del bloque a DISPONIBLE y desvincular la cita
        bloque.setEstadoBloque(EstadoBloque.DISPONIBLE);
        bloque.setCita(null);
        horarioBloqueRepository.save(bloque);

        // 5. Actualizar estado de la cita a CANCELADA
        cita.setEstadoCita(EstadoCita.CANCELADA);
        Cita citaCancelada = citaRepository.save(cita);

        return citaMapper.toResponse(citaCancelada);
    }

    @Transactional
    @Override
    public CitaResponse atenderCita(Long citaId) {
        // 1. Buscar la cita
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + citaId));

        // 2. Validar estado CONFIRMADA
        if (cita.getEstadoCita() != EstadoCita.CONFIRMADA) {
            throw new IllegalStateException("Solo se pueden atender citas en estado CONFIRMADA");
        }

        // 3. Validar relaciones
        if (cita.getPaciente() == null || cita.getMedico() == null || cita.getServicio() == null) {
            throw new IllegalStateException("La cita tiene datos incompletos");
        }

        // 4. Bloque asociado
        HorarioBloque bloque = horarioBloqueRepository.findByCita(cita)
                .orElseThrow(() -> new IllegalStateException("No hay bloque asociado a la cita"));

        // 5. Validar estado del bloque
        if (bloque.getEstadoBloque() != EstadoBloque.OCUPADO) {
            throw new IllegalStateException("El bloque debe estar OCUPADO para ser terminado");
        }

        // 6. Actualizar ambos
        bloque.setEstadoBloque(EstadoBloque.TERMINADO);
        horarioBloqueRepository.save(bloque);

        cita.setEstadoCita(EstadoCita.ATENDIDA);
        return citaMapper.toResponse(citaRepository.save(cita));
    }

    @Transactional
    @Override
    public CitaResponse reprogramarCita(Long citaId, CitaRequest nuevaCitaRequest) {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        // 1. Obtener la cita
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + citaId));

        // 2. Verificar estado válido para reprogramar
        if (!(cita.getEstadoCita() == EstadoCita.PENDIENTE ||
                cita.getEstadoCita() == EstadoCita.CONFIRMADA ||
                cita.getEstadoCita() == EstadoCita.CANCELADA ||
                cita.getEstadoCita() == EstadoCita.REPROGRAMADA)) {

            throw new IllegalStateException(
                    "Solo se pueden reprogramar citas en estado PENDIENTE, CONFIRMADA o CANCELADA");
        }

        // 3. Obtener el bloque anterior
        HorarioBloque bloqueAnterior = horarioBloqueRepository.findByCitaId(cita.getId())
                .orElseThrow(() -> new IllegalStateException("La cita no está asociada a un bloque horario"));

        // 4. Validar si la cita aún no ha pasado para liberar bloque anterior
        boolean citaNoPasada = cita.getFecha().isAfter(hoy)
                || (cita.getFecha().isEqual(hoy) && cita.getHora().isAfter(ahora));
        if (citaNoPasada) {
            bloqueAnterior.setEstadoBloque(EstadoBloque.DISPONIBLE);
            bloqueAnterior.setCita(null);
            horarioBloqueRepository.save(bloqueAnterior);
        }

        // 5. Validar que la nueva hora de la cita sea posterior a la actual
        boolean nuevaEsPasada = nuevaCitaRequest.getFecha().isBefore(hoy)
                || (nuevaCitaRequest.getFecha().isEqual(hoy) && nuevaCitaRequest.getHora().isBefore(ahora));
        if (nuevaEsPasada) {
            throw new IllegalArgumentException("No se puede reprogramar una cita para una fecha u hora pasada");
        }

        // 6. Buscar el nuevo bloque horario disponible
        HorarioBloque nuevoBloque = horarioBloqueRepository
                .findByFechaAndHoraInicioAndEstadoBloque(
                        nuevaCitaRequest.getFecha(),
                        nuevaCitaRequest.getHora(),
                        EstadoBloque.DISPONIBLE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No hay bloques DISPONIBLES para la fecha y hora proporcionadas"));

        if (nuevoBloque.getCita() != null) {
            throw new IllegalStateException("Ese bloque ya tiene una cita asignada");
        }

        // 7. Reprogramar la cita
        cita.setFecha(nuevaCitaRequest.getFecha());
        cita.setHora(nuevaCitaRequest.getHora());
        cita.setNotas(nuevaCitaRequest.getNotas());
        cita.setAntecedentes(nuevaCitaRequest.getAntecedentes());
        cita.setEstadoCita(EstadoCita.REPROGRAMADA);

        // 8. Enlazar al nuevo bloque
        nuevoBloque.setCita(cita);
        nuevoBloque.setEstadoBloque(EstadoBloque.OCUPADO);
        horarioBloqueRepository.save(nuevoBloque);

        Cita citaReprogramada = citaRepository.save(cita);
        return citaMapper.toResponse(citaReprogramada);
    }

    @Override
    public List<PacienteResponseDTO> obtenerPacientesPorMedicoConCitasConfirmadasOAtendidas(Long medicoId) {
        if (!medicoRepository.existsById(medicoId)) {
            throw new ResourceNotFoundException("No se encontró un médico con ID: " + medicoId);
        }

        List<EstadoCita> estados = List.of(EstadoCita.CONFIRMADA, EstadoCita.ATENDIDA);
        List<Paciente> pacientes = citaRepository.findPacientesByMedicoIdAndEstadoCitaIn(medicoId, estados);

        return pacientes.stream()
                .map(pacienteMapper::mapToPacienteResponseDTO)
                .distinct() // opcional, si hay duplicados
                .collect(Collectors.toList());
    }
}
