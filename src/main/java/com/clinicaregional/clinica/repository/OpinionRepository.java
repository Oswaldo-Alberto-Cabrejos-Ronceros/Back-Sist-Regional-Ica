package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Opinion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OpinionRepository extends JpaRepository<Opinion, Long> {

    Optional<Opinion> findByIdAndVisibleIsTrue(Long id);
}
