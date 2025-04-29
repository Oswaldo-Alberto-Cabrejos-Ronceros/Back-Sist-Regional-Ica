package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Alergia;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.entity.PacienteAlergia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PacienteAlergiaRepository extends JpaRepository<PacienteAlergia, Long> {
    Optional<PacienteAlergia> findByIdAndEstadoIsTrue(Long id);
    boolean existsByPacienteAndAlergia(Paciente paciente, Alergia alergia);
    List<PacienteAlergia> findByPaciente(Paciente paciente);
}
