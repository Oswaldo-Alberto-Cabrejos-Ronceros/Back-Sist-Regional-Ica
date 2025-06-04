package com.clinicaregional.clinica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicaregional.clinica.entity.Disponibilidad;
import com.clinicaregional.clinica.enums.DiaSemana;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {
    
    Optional<Disponibilidad> findByIdAndEstadoIsTrue(Long id);
    
    boolean existsByMedicoId(Long medicoId);
    
    Long countByMedicoId(Long medicoId);

    //para obtener las diponibilidades de un medico

    List<Disponibilidad> findAllByMedicoId(Long medicoId);

    boolean existsByMedicoIdAndDiaSemanaAndHoraInicioAndHoraFin(
            Long medicoId,
            DiaSemana diaSemana,
            LocalTime horaInicio,
            LocalTime horaFin);
    
}
