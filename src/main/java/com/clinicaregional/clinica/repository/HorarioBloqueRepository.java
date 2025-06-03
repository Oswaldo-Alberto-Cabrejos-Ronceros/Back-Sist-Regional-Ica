package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.HorarioBloque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HorarioBloqueRepository extends JpaRepository<HorarioBloque, Long> {
    //para obtener el horario bloque de un medico
    List<HorarioBloque> findByDisponibilidadId(Long disponibilidadId);

    List<HorarioBloque> findByDisponibilidad_Medico_Id(Long medicoId);

    List<HorarioBloque> findByFecha(LocalDate fecha);
}