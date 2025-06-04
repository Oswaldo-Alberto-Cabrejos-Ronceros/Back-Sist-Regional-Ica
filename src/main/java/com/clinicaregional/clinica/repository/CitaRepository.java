package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Cita;
import com.clinicaregional.clinica.enums.EstadoCita;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByEstadoCitaIn(List<EstadoCita> estados);

}
