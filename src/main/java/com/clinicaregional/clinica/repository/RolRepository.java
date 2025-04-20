
package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {
    boolean existsByNombre(String nombre);
}
