package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.entity.Cita;
import com.clinicaregional.clinica.entity.HorarioBloque;
import com.clinicaregional.clinica.enums.EstadoBloque;
import com.clinicaregional.clinica.enums.EstadoCita;
import com.clinicaregional.clinica.repository.CitaRepository;
import com.clinicaregional.clinica.repository.HorarioBloqueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CitaNoPresentadoScheduler {

    private final CitaRepository citaRepository;
    private final HorarioBloqueRepository horarioBloqueRepository;

    // @Scheduled(fixedRate = 10000) // cada 10 segundos
    @Scheduled(cron = "0 0 2 * * *") // Ejecuta cada día a las 2:00 AM
    @Transactional
    public void actualizarCitasNoPresentadas() {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        // Busca citas en estado PENDIENTE o CONFIRMADA
        List<Cita> citas = citaRepository.findByEstadoCitaIn(List.of(
                EstadoCita.PENDIENTE,
                EstadoCita.CONFIRMADA,
                EstadoCita.REPROGRAMADA));

        for (Cita cita : citas) {
            boolean fechaPasada = cita.getFecha().isBefore(hoy);
            boolean mismaFechaPeroHoraPasada = cita.getFecha().isEqual(hoy) && cita.getHora().isBefore(ahora);

            if (fechaPasada || mismaFechaPeroHoraPasada) {
                HorarioBloque bloque = horarioBloqueRepository.findByCitaId(cita.getId())
                        .orElse(null);

                if (bloque != null && (bloque.getEstadoBloque() == EstadoBloque.OCUPADO)) {
                    cita.setEstadoCita(EstadoCita.NO_PRESENTADO);
                    citaRepository.save(cita);

                    bloque.setEstadoBloque(EstadoBloque.EXPIRADO);
                    horarioBloqueRepository.save(bloque);
                }
            }
        }
    }
}
