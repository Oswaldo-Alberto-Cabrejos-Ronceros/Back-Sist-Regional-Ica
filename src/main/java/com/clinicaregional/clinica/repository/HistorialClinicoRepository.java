package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Long> {
    Optional<HistorialClinico> findByIdAndEstadoIsTrue(Long id);
    Optional<HistorialClinico> findByPaciente_Id(Long pacienteId);
    boolean existsByPaciente_Id(Long pacienteId);
}
