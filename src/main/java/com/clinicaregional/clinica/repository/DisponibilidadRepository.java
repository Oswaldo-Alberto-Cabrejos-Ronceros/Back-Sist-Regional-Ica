package com.clinicaregional.clinica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicaregional.clinica.entity.Disponibilidad;
import java.util.Optional;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {
    
    Optional<Disponibilidad> findByIdAndEstadoIsTrue(Long id);
    
    boolean existsByMedicoId(Long medicoId);
    
    Long countByMedicoId(Long medicoId);

    
}
