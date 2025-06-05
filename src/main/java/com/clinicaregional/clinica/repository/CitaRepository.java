package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Cita;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.enums.EstadoCita;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByEstadoCitaIn(List<EstadoCita> estados);

    @Query("SELECT DISTINCT c.paciente FROM Cita c WHERE c.medico.id = :medicoId AND c.estadoCita IN :estados")
    List<Paciente> findPacientesByMedicoIdAndEstadoCitaIn(@Param("medicoId") Long medicoId,
            @Param("estados") List<EstadoCita> estados);

}
