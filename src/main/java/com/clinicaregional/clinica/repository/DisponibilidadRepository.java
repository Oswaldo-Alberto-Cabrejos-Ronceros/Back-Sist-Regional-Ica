package com.clinicaregional.clinica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.clinicaregional.clinica.entity.Disponibilidad;
import com.clinicaregional.clinica.enums.DiaSemana;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

    Optional<Disponibilidad> findByIdAndEstadoIsTrue(Long id);

    boolean existsByMedicoId(Long medicoId);

    Long countByMedicoId(Long medicoId);

    // para obtener las diponibilidades de un medico

    List<Disponibilidad> findAllByMedicoId(Long medicoId);

    boolean existsByMedicoIdAndDiaSemanaAndHoraInicioAndHoraFin(
            Long medicoId,
            DiaSemana diaSemana,
            LocalTime horaInicio,
            LocalTime horaFin);

    @Query("""
                SELECT d FROM Disponibilidad d
                WHERE d.medico.id = :medicoId
                  AND d.diaSemana = :diaSemana
                  AND d.estado = true
                  AND (
                      (:horaInicio < d.horaFin AND :horaFin > d.horaInicio)
                  )
            """)
    List<Disponibilidad> findInterpuestas(
            @Param("medicoId") Long medicoId,
            @Param("diaSemana") DiaSemana diaSemana,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin);

}
