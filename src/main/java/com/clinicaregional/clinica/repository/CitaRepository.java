package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Cita;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.enums.EstadoCita;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByEstadoCitaIn(List<EstadoCita> estados);

    List<Cita> findByMedicoId(Long medicoId);

    @Query("SELECT DISTINCT c.paciente FROM Cita c WHERE c.medico.id = :medicoId AND c.estadoCita IN :estados")
    List<Paciente> findPacientesByMedicoIdAndEstadoCitaIn(@Param("medicoId") Long medicoId,
            @Param("estados") List<EstadoCita> estados);

    @Query("SELECT c FROM Cita c " +
            "WHERE c.medico.id = :medicoId " +
            "AND c.estadoCita = :estado " +
            "AND (c.fecha > CURRENT_DATE OR (c.fecha = CURRENT_DATE AND c.hora >= CURRENT_TIME))")
    List<Cita> findCitasConfirmadasFuturasPorMedico(
            @Param("medicoId") Long medicoId,
            @Param("estado") EstadoCita estado);

    List<Cita> findByMedicoIdAndEstadoCita(Long medicoId, EstadoCita estadoCita);

    // consulta que obtiene todas las citas CONFIRMADAS de un paciente cuya
    // fecha/hora son posteriores al momento actual
    // se ordena de forma cronológica por fecha y hora
    @Query("""
            SELECT c
            FROM Cita c
            WHERE
                c.paciente.id = :pacienteId
                AND c.estadoCita IN (com.clinicaregional.clinica.enums.EstadoCita.CONFIRMADA,
                                     com.clinicaregional.clinica.enums.EstadoCita.PENDIENTE)
                AND (
                    c.fecha > CURRENT_DATE
                    OR (c.fecha = CURRENT_DATE AND c.hora > CURRENT_TIME)
                )
            ORDER BY c.fecha ASC, c.hora ASC
            """)
    List<Cita> findCitasFuturasByPaciente(@Param("pacienteId") Long pacienteId);

    // Modificar la consulta para traer solo citas que ya pasaron
    @Query("SELECT c FROM Cita c WHERE c.estadoCita IN :estados " +
            "AND (c.fecha < :hoy OR (c.fecha = :hoy AND c.hora < :ahora))")
    List<Cita> findCitasVencidasNoPresentadas(
            @Param("estados") List<EstadoCita> estados,
            @Param("hoy") LocalDate hoy,
            @Param("ahora") LocalTime ahora);

    List<Cita> findAllByPaciente_Id(Long pacienteId);

    long countByFecha(LocalDate fecha);

    @Query("SELECT SUM(c.servicio.price) FROM Cita c WHERE MONTH(c.fecha) = :mes AND YEAR(c.fecha)= :year")
    Double sumarMontoServicioPorMes(@Param("mes") int mes, @Param("year") int year);

    List<Cita> findAllByFecha(LocalDate fecha);
}
