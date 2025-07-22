package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByNumeroIdentificacion(String numeroIdentificacion);
    Optional<Paciente> findByIdAndEstadoIsTrue(Long id);
    Optional<Paciente> findByUsuario_Id(Long id);
    Optional<Paciente> findByEmail(String email);
    Page<Paciente> findAllByEstadoIsTrue(Pageable pageable);
    boolean existsByNumeroIdentificacion(String numeroIdentificacion);
    long countByEstadoIsTrue();
}
