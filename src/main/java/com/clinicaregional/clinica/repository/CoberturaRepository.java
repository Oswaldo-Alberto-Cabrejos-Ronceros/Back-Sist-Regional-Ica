package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Cobertura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoberturaRepository extends JpaRepository<Cobertura, Long> {
    Optional<Cobertura> findByIdAndEstadoIsTrue(Long id);

    boolean existsByNombre(String nombre);
}
