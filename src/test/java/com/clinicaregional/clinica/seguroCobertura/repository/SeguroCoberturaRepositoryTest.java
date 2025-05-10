package com.clinicaregional.clinica.seguroCobertura.repository;

import com.clinicaregional.clinica.entity.Cobertura;
import com.clinicaregional.clinica.entity.Seguro;
import com.clinicaregional.clinica.entity.SeguroCobertura;
import com.clinicaregional.clinica.repository.SeguroCoberturaRepository;
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
class SeguroCoberturaRepositoryTest {

    @Autowired
    private SeguroCoberturaRepository seguroCoberturaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Seguro seguro;
    private Cobertura cobertura;

    @BeforeEach
    void setUp() {
        Session session = entityManager.getEntityManager().unwrap(Session.class);
        Filter filter = session.enableFilter("estadoActivo");
        filter.setParameter("estado", true);

        seguro = Seguro.builder()
                .nombre("RIMAC")
                .descripcion("Seguro nacional")
                .estado(true)
                .build();

        cobertura = Cobertura.builder()
                .nombre("Cobertura Total")
                .descripcion("Incluye todos los servicios")
                .estado(true)
                .build();

        entityManager.persistAndFlush(seguro);
        entityManager.persistAndFlush(cobertura);
    }

    @Test
    @DisplayName("Guardar SeguroCobertura con estado TRUE y buscar por ID")
    void guardarSeguroCobertura_conEstadoTrue_debeEncontrarsePorId() {
        // Arrange
        SeguroCobertura sc = SeguroCobertura.builder()
                .seguro(seguro)
                .cobertura(cobertura)
                .estado(true)
                .build();
        entityManager.persistAndFlush(sc);

        // Act
        Optional<SeguroCobertura> resultado = seguroCoberturaRepository.findByIdAndEstadoIsTrue(sc.getId());

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getSeguro().getNombre()).isEqualTo("RIMAC");
        assertThat(resultado.get().getCobertura().getNombre()).isEqualTo("Cobertura Total");
    }

    @Test
    @DisplayName("Guardar SeguroCobertura con estado FALSE y verificar que no se encuentre por ID")
    void guardarSeguroCobertura_conEstadoFalse_noDebeEncontrarsePorId() {
        // Arrange
        SeguroCobertura sc = SeguroCobertura.builder()
                .seguro(seguro)
                .cobertura(cobertura)
                .estado(false)
                .build();
        entityManager.persistAndFlush(sc);

        // Act
        Optional<SeguroCobertura> resultado = seguroCoberturaRepository.findByIdAndEstadoIsTrue(sc.getId());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Buscar por ID de seguro debe retornar lista de relaciones")
    void findBySeguroId_debeRetornarRelaciones() {
        // Arrange
        SeguroCobertura sc = SeguroCobertura.builder()
                .seguro(seguro)
                .cobertura(cobertura)
                .estado(true)
                .build();
        entityManager.persistAndFlush(sc);

        // Act
        List<SeguroCobertura> resultado = seguroCoberturaRepository.findBySeguro_Id(seguro.getId());

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCobertura().getDescripcion()).isEqualTo("Incluye todos los servicios");
    }

    @Test
    @DisplayName("Buscar por ID de cobertura debe retornar lista de relaciones")
    void findByCoberturaId_debeRetornarRelaciones() {
        // Arrange
        SeguroCobertura sc = SeguroCobertura.builder()
                .seguro(seguro)
                .cobertura(cobertura)
                .estado(true)
                .build();
        entityManager.persistAndFlush(sc);

        // Act
        List<SeguroCobertura> resultado = seguroCoberturaRepository.findByCobertura_Id(cobertura.getId());

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getSeguro().getDescripcion()).isEqualTo("Seguro nacional");
    }

    @Test
    @DisplayName("Verificar existencia por IDs de seguro y cobertura")
    void existsBySeguroAndCobertura_debeRetornarTrueSiExiste() {
        // Arrange
        SeguroCobertura sc = SeguroCobertura.builder()
                .seguro(seguro)
                .cobertura(cobertura)
                .estado(true)
                .build();
        entityManager.persistAndFlush(sc);

        // Act
        boolean existe = seguroCoberturaRepository.existsBySeguro_IdAndCobertura_Id(
                seguro.getId(), cobertura.getId()
        );

        // Assert
        assertThat(existe).isTrue();
    }
}
