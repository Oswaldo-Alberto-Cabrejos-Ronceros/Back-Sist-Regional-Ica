package com.clinicaregional.clinica.repository;
import com.clinicaregional.clinica.entity.Cita;
import com.clinicaregional.clinica.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


public interface CitaRepository extends JpaRepository<Cita, Long> {
  boolean existsByFechaHoraAndEstadoCitaNotAndEstadoCitaNot(LocalDateTime fechaHora, EstadoCita estadocita, EstadoCita estadoCitaSecond);
} 
