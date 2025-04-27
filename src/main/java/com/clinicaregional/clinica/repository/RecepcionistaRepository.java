package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Recepcionista;
import com.clinicaregional.clinica.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecepcionistaRepository extends JpaRepository<Recepcionista, Long>{
    Optional<Recepcionista> findByIdAndEstadoIsTrue(Long id);
    boolean existsByNumeroDocumento(String numeroDocumento);
    boolean existsByUsuario(Usuario usuario);
}
