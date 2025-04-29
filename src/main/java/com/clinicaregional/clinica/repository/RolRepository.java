
package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    boolean existsByNombre(String nombre);
    boolean existsByNombreAndEstadoIsTrue(String nombre);
    Optional<Rol> findByIdAndEstadoIsTrue(Long id);
}
