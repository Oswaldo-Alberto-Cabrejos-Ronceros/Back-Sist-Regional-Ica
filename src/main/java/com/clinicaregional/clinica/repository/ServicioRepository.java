package com.clinicaregional.clinica.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinicaregional.clinica.entity.Servicio;

public interface  ServicioRepository extends JpaRepository<Servicio, Long> {
   Optional<Servicio> findByIdAndEstadoIsTrue(Long id);
    
}
