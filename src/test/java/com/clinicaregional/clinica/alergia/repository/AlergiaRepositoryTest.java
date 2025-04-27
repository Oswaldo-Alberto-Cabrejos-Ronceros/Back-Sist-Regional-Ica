package com.clinicaregional.clinica.alergia.repository;

import com.clinicaregional.clinica.entity.Alergia;
import com.clinicaregional.clinica.enums.TipoAlergia;
import com.clinicaregional.clinica.repository.AlergiaRepository;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AlergiaRepositoryTest {

    @Autowired
    private AlergiaRepository alergiaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void activarFiltroEstado() {
        Session session = entityManager.getEntityManager().unwrap(Session.class);
        Filter filter = session.enableFilter("estadoActivo");
        filter.setParameter("estado", true);
    }

    @Test
    void guardarAlergia_conEstadoTrue_debeEncontrarlaPorId() {
        // Arrange
        Alergia alergia = Alergia.builder()
                .nombre("Polen")
                .tipoAlergia(TipoAlergia.AMBIENTAL)
                .estado(true)
                .build();
        alergiaRepository.save(alergia);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<Alergia> encontrada = alergiaRepository.findByIdAndEstadoIsTrue(alergia.getId());

        // Assert
        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getNombre()).isEqualTo("Polen");
    }

    @Test
    void guardarAlergia_conEstadoFalse_noDebeEncontrarlaPorId() {
        // Arrange
        Alergia alergia = Alergia.builder()
                .nombre("Pelo de gato")
                .tipoAlergia(TipoAlergia.AMBIENTAL)
                .estado(false)
                .build();
        alergiaRepository.save(alergia);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<Alergia> encontrada = alergiaRepository.findByIdAndEstadoIsTrue(alergia.getId());

        // Assert
        assertThat(encontrada).isEmpty();
    }

    @Test
    void listarAlergias_debeRetornarSoloActivas() {
        // Arrange
        Alergia activa = Alergia.builder()
                .nombre("Ácaros")
                .tipoAlergia(TipoAlergia.AMBIENTAL)
                .estado(true)
                .build();

        Alergia inactiva = Alergia.builder()
                .nombre("Medicamentos")
                .tipoAlergia(TipoAlergia.MEDICA)
                .estado(false)
                .build();

        alergiaRepository.save(activa);
        alergiaRepository.save(inactiva);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<Alergia> alergias = alergiaRepository.findAll();

        // Assert
        assertThat(alergias).allMatch(a -> Boolean.TRUE.equals(a.getEstado()));
    }

    @Test
    void buscarAlergias_porTipoAlergia_debeRetornarCorrectas() {
        // Arrange
        Alergia alergia1 = Alergia.builder()
                .nombre("Penicilina")
                .tipoAlergia(TipoAlergia.MEDICA)
                .estado(true)
                .build();

        Alergia alergia2 = Alergia.builder()
                .nombre("Polvo")
                .tipoAlergia(TipoAlergia.AMBIENTAL)
                .estado(true)
                .build();

        alergiaRepository.save(alergia1);
        alergiaRepository.save(alergia2);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<Alergia> medicamentos = alergiaRepository.findByTipoAlergia(TipoAlergia.MEDICA);

        // Assert
        assertThat(medicamentos).hasSize(1);
        assertThat(medicamentos.get(0).getNombre()).isEqualTo("Penicilina");
    }
}
