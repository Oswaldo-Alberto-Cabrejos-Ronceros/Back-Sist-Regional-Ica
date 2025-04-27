package com.clinicaregional.clinica.repository;
import com.clinicaregional.clinica.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    Optional<Medico> findByIdAndEstadoIsTrue(Long id);
}
