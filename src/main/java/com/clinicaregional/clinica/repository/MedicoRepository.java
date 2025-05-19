package com.clinicaregional.clinica.repository;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    Optional<Medico> findByIdAndEstadoIsTrue(Long id);
    boolean existsByNumeroColegiatura(String numeroColegiatura);
    boolean existsByNumeroRNE(String numeroRNE);
    boolean existsByUsuario(Usuario usuario);
    Optional<Medico> findByUsuario_Id(Long id);

    boolean existsByNumeroColegiaturaAndIdNot(String numeroColegiatura, Long id);
    boolean existsByNumeroRNEAndIdNot(String numeroRNE, Long id);
    boolean existsByUsuarioAndIdNot(Usuario usuario, Long id);
}
