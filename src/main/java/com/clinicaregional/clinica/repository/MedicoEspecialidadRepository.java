package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.MedicoEspecialidad;
import com.clinicaregional.clinica.entity.MedicoEspecialidadId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicoEspecialidadRepository extends JpaRepository<MedicoEspecialidad, MedicoEspecialidadId> {
    List<MedicoEspecialidad> findByMedicoId(Long medicoId);
    List<MedicoEspecialidad> findByEspecialidadId(Long especialidadId);
    Optional<MedicoEspecialidad> findByIdAndEstadoIsTrue(MedicoEspecialidadId id);

}