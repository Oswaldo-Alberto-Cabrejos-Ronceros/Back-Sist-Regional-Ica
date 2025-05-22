package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Seguro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeguroRepository extends JpaRepository<Seguro, Long> {
    Optional<Seguro> findByIdAndEstadoIsTrue(Long id);
    boolean existsByNombre(String nombre);
    Optional<Seguro> findByNombre(String nombre);
}
