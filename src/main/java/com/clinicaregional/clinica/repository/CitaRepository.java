package com.clinicaregional.clinica.repository;
import com.clinicaregional.clinica.entity.Cita;
import com.clinicaregional.clinica.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;


public interface CitaRepository extends JpaRepository<Cita, Long> {
  boolean existsByFechaAndHoraAndEstadoCitaNotAndEstadoCitaNot(LocalDate fecha, LocalTime hora, EstadoCita estadocita, EstadoCita estadoCitaSecond);
} 
