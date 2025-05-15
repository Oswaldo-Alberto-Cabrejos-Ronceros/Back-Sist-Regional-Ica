package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.PacienteDTO;
import com.clinicaregional.clinica.dto.SeguroDTO;
import com.clinicaregional.clinica.dto.ServicioSeguroDTO;
import com.clinicaregional.clinica.dto.request.CitaReprogramarRequest;
import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
import com.clinicaregional.clinica.entity.Cita;
import com.clinicaregional.clinica.entity.Cobertura;
import com.clinicaregional.clinica.entity.Seguro;
import com.clinicaregional.clinica.entity.Servicio;
import com.clinicaregional.clinica.enums.EstadoCita;
import com.clinicaregional.clinica.enums.EstadoSeguro;
import com.clinicaregional.clinica.mapper.CitaMapper;
import com.clinicaregional.clinica.repository.CitaRepository;
import com.clinicaregional.clinica.repository.MedicoRepository;
import com.clinicaregional.clinica.repository.ServicioRepository;
import com.clinicaregional.clinica.service.CitaService;
import com.clinicaregional.clinica.service.PacienteService;
import com.clinicaregional.clinica.service.SeguroService;
import com.clinicaregional.clinica.service.ServicioSeguroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;
    private final PacienteService pacienteService;
    private final SeguroService seguroService;
    private final ServicioSeguroService servicioSeguroService;
    private final MedicoRepository medicoRepository;
    private final ServicioRepository servicioRepository;

    @Transactional(readOnly = true)
    @Override
    public List<CitaResponse> listar() {
        List<Cita> citas = citaRepository.findAll();
        return citas.stream().map(citaMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<CitaResponse> obtenerPorId(Long id) {
        return citaRepository.findById(id).map(citaMapper::toResponse);
    }

    @Transactional
    @Override
    public CitaResponse guardar(CitaRequest citaRequest) {
        // Validar disponibilidad de fecha y hora
        if (citaRepository.existsByFechaHoraAndEstadoCitaNotAndEstadoCitaNot(
                citaRequest.getFechaHora(), EstadoCita.CANCELADA, EstadoCita.EN_CURSO)) {
            throw new RuntimeException("Fecha y hora no disponibles");
        }

        // Verificar que el paciente existe
        PacienteDTO paciente = pacienteService.getPacientePorId(citaRequest.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Verificar que el médico existe
        if (citaRequest.getMedicoId() == null) {
            throw new RuntimeException("Debe seleccionar un médico");
        }

        boolean medicoExiste = medicoRepository.existsById(citaRequest.getMedicoId());
        if (!medicoExiste) {
            throw new RuntimeException("Médico no encontrado");
        }

        Cita cita = citaMapper.toEntity(citaRequest);

        // Validar si el seguro cubre el servicio
        if (paciente.getSeguroId() != null) {
            SeguroDTO seguro = seguroService.getSeguroById(paciente.getSeguroId())
                    .orElseThrow(() -> new RuntimeException("Seguro no encontrado"));

            if (seguro.getEstadoSeguro() != EstadoSeguro.INACTIVO) {
                Long servicioId = citaRequest.getServicioId();
                ServicioSeguroDTO servicioSeguro = servicioSeguroService
                        .getSeguroServicioBySeguroAndServicio(paciente.getSeguroId(), servicioId)
                        .orElse(null);

                if (servicioSeguro == null) {
                    throw new RuntimeException("El seguro no cubre el servicio seleccionado");
                }

                Seguro seguroAdd = new Seguro();
                seguroAdd.setId(servicioSeguro.getId());

                Cobertura coberturaAdd = new Cobertura();
                coberturaAdd.setId(servicioSeguro.getId());

                cita.setSeguro(seguroAdd);
                cita.setCobertura(coberturaAdd);
            }
        }

        Cita savedCita = citaRepository.save(cita);
        return citaMapper.toResponse(savedCita);
    }


    @Transactional
    @Override
    public CitaResponse actualizar(Long id, CitaRequest citaRequest) {
        Cita existingCita = citaRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro la cita"));
        if (citaRepository.existsByFechaHoraAndEstadoCitaNotAndEstadoCitaNot(citaRequest.getFechaHora(), EstadoCita.CANCELADA, EstadoCita.EN_CURSO)) {
            throw new RuntimeException("Fecha y Hora no disponibles");
        }
        existingCita.setFechaHora(citaRequest.getFechaHora());
        existingCita.setEstadoCita(citaRequest.getEstadoCita());
        existingCita.setNotas(citaRequest.getNotas());
        existingCita.setAntecedentes(citaRequest.getAntecedentes());
        Cita updatedCita = citaRepository.save(existingCita);
        return citaMapper.toResponse(updatedCita);

    }

    @Transactional
    @Override
    public void cancelar(Long id) {
        Cita cita = citaRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro la cita"));
        if (cita.getEstadoCita() == EstadoCita.CANCELADA) {
            throw new RuntimeException("La cita ya esta cancelada");
        }
        if (cita.getEstadoCita() == EstadoCita.ATENDIDA) {
            throw new RuntimeException("La cita ya ha sido atendida");
        }
        cita.setEstadoCita(EstadoCita.CANCELADA);
        citaRepository.save(cita);
    }

    @Transactional
    @Override
    public CitaResponse reprogramar(Long id, CitaReprogramarRequest citaReprogramarRequest) {
        Cita cita = citaRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro la cita"));
        if (citaRepository.existsByFechaHoraAndEstadoCitaNotAndEstadoCitaNot(citaReprogramarRequest.getFechaHora(), EstadoCita.CANCELADA, EstadoCita.EN_CURSO)) {
            throw new RuntimeException("Fecha y Hora no disponibles");
        }
        if (cita.getEstadoCita() == EstadoCita.CANCELADA) {
            throw new RuntimeException("La cita ya esta cancelada");
        }
        if (cita.getEstadoCita() == EstadoCita.ATENDIDA) {
            throw new RuntimeException("La cita ya ha sido atendida");
        }
        cita.setEstadoCita(EstadoCita.REPROGRAMADA);
        cita.setFechaHora(citaReprogramarRequest.getFechaHora());
        citaRepository.save(cita);
        return citaMapper.toResponse(cita);
    }

    @Transactional
    @Override
    public void marcarAtentida(Long id) {
        Cita cita = citaRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro la cita"));
        if (cita.getEstadoCita() == EstadoCita.EN_CURSO) {
            cita.setEstadoCita(EstadoCita.ATENDIDA);
            citaRepository.save(cita);
        } else {
            throw new RuntimeException("La cita no esta en curso");
        }
    }

    @Transactional
    @Override
    public void marcoNoAsistio(Long id) {
        Cita cita = citaRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro la cita"));
        if (cita.getEstadoCita() == EstadoCita.CANCELADA) {
            throw new RuntimeException("La cita ya esta cancelada");
        }
        if (cita.getEstadoCita() == EstadoCita.ATENDIDA) {
            throw new RuntimeException("La cita ya ha sido atendida");
        }
        if (cita.getEstadoCita() == EstadoCita.NO_ASISTIO) {
            throw new RuntimeException("La cita ya esta marcada como No Asistio");
        }
        cita.setEstadoCita(EstadoCita.NO_ASISTIO);
        citaRepository.save(cita);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Servicio> listarServiciosActivos() {
        return servicioRepository.findAllByEstadoTrue();
    }

    @Transactional(readOnly = true)
    @Override
    public Servicio obtenerServicioPorId(Long id) {
        return servicioRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado o inactivo"));
    }

}
