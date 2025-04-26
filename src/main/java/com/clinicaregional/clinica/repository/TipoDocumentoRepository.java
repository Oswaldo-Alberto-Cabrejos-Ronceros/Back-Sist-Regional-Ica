package com.clinicaregional.clinica.repository;


import com.clinicaregional.clinica.entity.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long> {
    boolean existsByNombreAndEstadoIsTrue(String nombre);
    Optional<TipoDocumento> findByIdAndEstadoIsTrue(Long id);

}
