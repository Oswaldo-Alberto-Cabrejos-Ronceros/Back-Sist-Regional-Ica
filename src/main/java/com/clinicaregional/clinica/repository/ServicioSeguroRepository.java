package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.ServicioSeguro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServicioSeguroRepository extends JpaRepository<ServicioSeguro, Long> {
    Optional<ServicioSeguro> findByIdAndEstadoIsTrue(Long id);
    List<ServicioSeguro> findByServicio_Id(Long id);
    List<ServicioSeguro> findBySeguro_Id(Long id);
    List<ServicioSeguro> findByCobertura_Id(Long id);
    boolean existsByServicio_IdAndSeguro_IdAndCobertura_Id(Long servicioId, Long seguroId, Long coberturaId);
}
