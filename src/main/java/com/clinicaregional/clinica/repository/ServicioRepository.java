package com.clinicaregional.clinica.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinicaregional.clinica.entity.Servicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    Optional<Servicio> findByIdAndEstadoIsTrue(Long id);

    boolean existsByNombre(String nombre);

    List<Servicio> findAllByEspecialidad_Id(Long id);

    boolean existsByNombreAndIdNot(String nombre, Long id);

    Page<Servicio> findAllByEstadoIsTrue(Pageable pageable);
}
