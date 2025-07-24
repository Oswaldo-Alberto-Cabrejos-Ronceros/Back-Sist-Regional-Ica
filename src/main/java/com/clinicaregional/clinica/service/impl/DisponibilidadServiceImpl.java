package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.request.DisponibilidadRequest;
import com.clinicaregional.clinica.dto.response.DisponibilidadResponse;
import com.clinicaregional.clinica.entity.Disponibilidad;
import com.clinicaregional.clinica.entity.HorarioBloque;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.enums.EstadoBloque;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.DisponibilidadMapper;
import com.clinicaregional.clinica.repository.DisponibilidadRepository;
import com.clinicaregional.clinica.repository.HorarioBloqueRepository;
import com.clinicaregional.clinica.repository.MedicoRepository;
import com.clinicaregional.clinica.service.DisponibilidadService;
import com.clinicaregional.clinica.util.FechaUtil;
import com.clinicaregional.clinica.util.FiltroEstado;
import com.clinicaregional.clinica.util.HorarioBloqueGenerator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisponibilidadServiceImpl implements DisponibilidadService {

        private final DisponibilidadRepository disponibilidadRepository;
        private final MedicoRepository medicoRepository;
        private final FiltroEstado filtroEstado;
        private final DisponibilidadMapper disponibilidadMapper;
        private final HorarioBloqueRepository horarioBloqueRepository;

        @Transactional
        @Override
        public DisponibilidadResponse registrar(DisponibilidadRequest request) {
                filtroEstado.activarFiltroEstado(true);

                medicoRepository.findByIdAndEstadoIsTrue(request.getMedicoId())
                                .orElseThrow(
                                                () -> new ResourceNotFoundException("Médico no encontrado con ID: "
                                                                + request.getMedicoId()));

                if (disponibilidadRepository.existsByMedicoIdAndDiaSemanaAndHoraInicioAndHoraFin(
                                request.getMedicoId(),
                                request.getDiaSemana(),
                                request.getHoraInicio(),
                                request.getHoraFin())) {
                        throw new IllegalArgumentException(
                                        "Ya existe una disponibilidad para ese médico en el mismo horario y día.");
                }

                if (!disponibilidadRepository.findInterpuestas(
                                request.getMedicoId(),
                                request.getDiaSemana(),
                                request.getHoraInicio(),
                                request.getHoraFin()).isEmpty()) {
                        throw new IllegalArgumentException(
                                        "Ya existe una disponibilidad que se solapa en ese horario.");
                }

                Disponibilidad disponibilidad = disponibilidadMapper.toEntity(request);
                Disponibilidad guardada = disponibilidadRepository.save(disponibilidad);

                List<LocalDate> fechas = FechaUtil.obtenerFechasDelMes(
                                request.getDiaSemana().toJavaDayOfWeek(),
                                YearMonth.now() // usa el mes actual
                );

                for (LocalDate fecha : fechas) {
                        List<HorarioBloque> bloques = HorarioBloqueGenerator.generarBloques(
                                        guardada,
                                        fecha,
                                        request.getDuracionMinutos());
                        horarioBloqueRepository.saveAll(bloques);
                }

                return disponibilidadMapper.toResponse(guardada);
        }

        @Transactional(readOnly = true)
        @Override
        public DisponibilidadResponse obtenerPorId(Long id) {
                filtroEstado.activarFiltroEstado(true);
                Disponibilidad disponibilidad = disponibilidadRepository.findByIdAndEstadoIsTrue(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Disponibilidad no encontrada con ID: " + id));
                return disponibilidadMapper.toResponse(disponibilidad);
        }

        @Transactional(readOnly = true)
        @Override
        public List<DisponibilidadResponse> listar() {
                filtroEstado.activarFiltroEstado(true);
                return disponibilidadRepository.findAll().stream().map(disponibilidadMapper::toResponse)
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        @Override
        public List<DisponibilidadResponse> listarPorMedicoId(Long medicoId) {
                medicoRepository.findByIdAndEstadoIsTrue(medicoId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Médico no encontrado con ID: " + medicoId));

                filtroEstado.activarFiltroEstado(true);
                return disponibilidadRepository.findAllByMedicoId(medicoId).stream()
                                .map(disponibilidadMapper::toResponse)
                                .collect(Collectors.toList());
        }

        @Transactional
        @Override
        public DisponibilidadResponse actualizar(Long id, DisponibilidadRequest request) {
                filtroEstado.activarFiltroEstado(true);
                Disponibilidad disponibilidad = disponibilidadRepository.findByIdAndEstadoIsTrue(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Disponibilidad no encontrada con ID: " + id));

                if (!disponibilidad.getMedico().getId().equals(request.getMedicoId())) {
                        Medico nuevoMedico = medicoRepository.findByIdAndEstadoIsTrue(request.getMedicoId())
                                        .orElseThrow(
                                                        () -> new ResourceNotFoundException(
                                                                        "Médico no encontrado con ID: "
                                                                                        + request.getMedicoId()));
                        disponibilidad.setMedico(nuevoMedico);
                }

                disponibilidad.setDiaSemana(request.getDiaSemana());
                disponibilidad.setHoraInicio(request.getHoraInicio());
                disponibilidad.setHoraFin(request.getHoraFin());
                disponibilidad.setNotas(request.getNotas());

                Disponibilidad actualizada = disponibilidadRepository.save(disponibilidad);

                return disponibilidadMapper.toResponse(actualizada);
        }

        @Transactional
        public void eliminar(Long disponibilidadId) {
                Disponibilidad disponibilidad = disponibilidadRepository.findByIdAndEstadoIsTrue(disponibilidadId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "No existe la disponibilidad activa con ID: " + disponibilidadId));

                List<HorarioBloque> bloques = horarioBloqueRepository.findByDisponibilidadId(disponibilidadId);

                for (HorarioBloque bloque : bloques) {
                        if (bloque.getEstadoBloque() == EstadoBloque.DISPONIBLE) {
                                bloque.setEstadoBloque(EstadoBloque.EXPIRADO);
                        }
                }

                horarioBloqueRepository.saveAll(bloques);

                disponibilidad.setEstado(false);
                disponibilidadRepository.save(disponibilidad);
        }

}
