package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.HorarioBloque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HorarioBloqueRepository extends JpaRepository<HorarioBloque, Long> {

}