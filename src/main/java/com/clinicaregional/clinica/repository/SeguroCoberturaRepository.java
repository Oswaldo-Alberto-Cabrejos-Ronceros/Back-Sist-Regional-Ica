package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.SeguroCobertura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeguroCoberturaRepository extends JpaRepository<SeguroCobertura, Long> {
    Optional<SeguroCobertura> findByIdAndEstadoIsTrue(Long id);
    boolean existsBySeguro_IdAndCobertura_Id(Long seguroId, Long coberturaId);
    List<SeguroCobertura> findBySeguro_Id(Long seguroId);
    List<SeguroCobertura> findByCobertura_Id(Long coberturaId);
}
