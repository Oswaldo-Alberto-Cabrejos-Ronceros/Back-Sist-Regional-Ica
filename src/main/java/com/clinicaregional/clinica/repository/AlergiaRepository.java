package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Alergia;
import com.clinicaregional.clinica.enums.TipoAlergia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlergiaRepository extends JpaRepository<Alergia, Long> {
    boolean existsByNombreAndEstadoIsTrue(String nombre);
    Optional<Alergia> findByIdAndEstadoIsTrue(Long id);
    List<Alergia> findByTipoAlergia(TipoAlergia tipoAlergia);
    boolean existsByNombreAndEstadoIsTrueAndIdNot(String nombre, Long id);
}
