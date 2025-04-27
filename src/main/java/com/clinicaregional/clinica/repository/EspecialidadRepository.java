package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {
    // Aquí puedes agregar métodos personalizados si es necesario
    Optional<Especialidad> findByIdAndEstadoIsTrue(Long id);
}
