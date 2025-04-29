package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByNumeroIdentificacion(String numeroIdentificacion);
    Optional<Paciente> findByIdAndEstadoIsTrue(Long id);
    Optional<Paciente> findByUsuario_Id(Long id);
}
