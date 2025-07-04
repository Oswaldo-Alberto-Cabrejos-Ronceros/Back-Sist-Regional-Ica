package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.entity.Cita;
import com.clinicaregional.clinica.entity.HorarioBloque;
import com.clinicaregional.clinica.enums.EstadoBloque;
import com.clinicaregional.clinica.enums.EstadoCita;
import com.clinicaregional.clinica.repository.CitaRepository;
import com.clinicaregional.clinica.repository.HorarioBloqueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CitaNoPresentadoScheduler {

    private final CitaRepository citaRepository;
    private final HorarioBloqueRepository horarioBloqueRepository;

    // Margen de tolerancia en minutos para considerar una cita como no presentada
    private static final int MARGEN_TOLERANCIA_MINUTOS = 15;

    @Scheduled(fixedRate = 1_800_000) // Ejecución cada 30 minutos (1,800,000 ms)
    @Transactional
    public void actualizarCitasNoPresentadas() {
        ZoneId zonaHoraria = ZoneId.of("America/Lima"); // Zona horaria de Perú
        LocalDate hoy = LocalDate.now(zonaHoraria);
        LocalTime ahora = LocalTime.now(zonaHoraria);
        LocalTime ahoraConMargen = ahora.minusMinutes(MARGEN_TOLERANCIA_MINUTOS);

        log.info("Iniciando proceso de actualización de citas no presentadas. Fecha: {}, Hora: {}", hoy, ahora);

        // Usamos la consulta optimizada que solo trae citas vencidas
        List<Cita> citasVencidas = citaRepository.findCitasVencidasNoPresentadas(
                List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA, EstadoCita.REPROGRAMADA),
                hoy,
                ahoraConMargen);

        int citasActualizadas = 0;
        int citasConProblemas = 0;

        for (Cita cita : citasVencidas) {
            try {
                HorarioBloque bloque = horarioBloqueRepository.findByCitaId(cita.getId())
                        .orElse(null);

                if (bloque != null && bloque.getEstadoBloque() == EstadoBloque.OCUPADO) {
                    // Actualizar cita
                    cita.setEstadoCita(EstadoCita.NO_PRESENTADO);
                    citaRepository.save(cita);

                    // Actualizar bloque
                    bloque.setEstadoBloque(EstadoBloque.EXPIRADO);
                    horarioBloqueRepository.save(bloque);

                    citasActualizadas++;

                    // Log detallado
                    log.info("Cita actualizada - ID: {}, Fecha: {}, Hora: {}, Médico: {}, Paciente: {}, Bloque: {}",
                            cita.getId(),
                            cita.getFecha(),
                            cita.getHora(),
                            cita.getMedico() != null ? cita.getMedico().getId() : "N/A",
                            cita.getPaciente() != null ? cita.getPaciente().getId() : "N/A",
                            bloque.getId());
                } else {
                    log.warn("Cita ID {} no tiene bloque asociado o bloque no está OCUPADO", cita.getId());
                    citasConProblemas++;
                }
            } catch (Exception e) {
                log.error("Error al procesar cita ID {}: {}", cita.getId(), e.getMessage());
                citasConProblemas++;
            }
        }

        log.info("Proceso completado. Citas actualizadas: {}, Citas con problemas: {}, Total procesadas: {}",
                citasActualizadas,
                citasConProblemas,
                citasVencidas.size());

        if (citasConProblemas > 0) {
            log.warn("Se encontraron {} citas con problemas que requieren revisión manual", citasConProblemas);
        }
    }
}