package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {
    // Aquí puedes agregar métodos personalizados si es necesario
    
}
