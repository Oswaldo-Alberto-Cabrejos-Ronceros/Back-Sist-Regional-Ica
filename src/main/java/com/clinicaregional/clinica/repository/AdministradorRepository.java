package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    Optional<Administrador> findByIdAndEstadoIsTrue(Long id);
    boolean existsByNumeroDocumento(String numeroDocumento);
    boolean existsByUsuario_Id(Long id);
}
