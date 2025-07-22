package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Resultado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultadoRepository extends JpaRepository<Resultado, Long> {
    Optional<Resultado> findByIdAndEstadoIsTrue(Long id);

    //obtener resultados de una cita
    List<Resultado> findAllByCita_Id(Long citaId);

    //obtener resultados de un historial clinico
    List<Resultado> findAllByHistorialClinico_Id(Long clinicoId);

    //obtener resultados de un paciente mediante su historial clinico
    List<Resultado> findAllByHistorialClinico_Paciente_Id(Long pacienteId);

}