package com.clinicaregional.clinica.cobertura.repository;

import com.clinicaregional.clinica.entity.Cobertura;
import com.clinicaregional.clinica.repository.CoberturaRepository;
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
class CoberturaRepositoryTest {

    @Autowired
    private CoberturaRepository coberturaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void activarFiltroEstado() {
        Session session = entityManager.getEntityManager().unwrap(Session.class);
        Filter filter = session.enableFilter("estadoActivo");
        filter.setParameter("estado", true);
    }

    @Test
    @DisplayName("Guardar cobertura con estado TRUE debe ser encontrada por ID")
    void guardarCobertura_conEstadoTrue_debeEncontrarsePorId() {
        // Arrange
        Cobertura cobertura = Cobertura.builder()
                .nombre("Cobertura General")
                .descripcion("Aplica a todos los servicios")
                .estado(true)
                .build();
        coberturaRepository.save(cobertura);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<Cobertura> encontrada = coberturaRepository.findByIdAndEstadoIsTrue(cobertura.getId());

        // Assert
        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getNombre()).isEqualTo("Cobertura General");
    }

    @Test
    @DisplayName("Guardar cobertura con estado FALSE no debe ser encontrada por ID")
    void guardarCobertura_conEstadoFalse_noDebeEncontrarsePorId() {
        // Arrange
        Cobertura cobertura = Cobertura.builder()
                .nombre("Cobertura Eliminada")
                .descripcion("No está disponible")
                .estado(false)
                .build();
        coberturaRepository.save(cobertura);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<Cobertura> encontrada = coberturaRepository.findByIdAndEstadoIsTrue(cobertura.getId());

        // Assert
        assertThat(encontrada).isEmpty();
    }

    @Test
    @DisplayName("Listar coberturas solo retorna las activas")
    void listarCoberturas_soloActivas() {
        // Arrange
        Cobertura activa = Cobertura.builder()
                .nombre("Cobertura Básica")
                .descripcion("Disponible")
                .estado(true)
                .build();

        Cobertura inactiva = Cobertura.builder()
                .nombre("Cobertura Obsoleta")
                .descripcion("Retirada del sistema")
                .estado(false)
                .build();

        coberturaRepository.save(activa);
        coberturaRepository.save(inactiva);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<Cobertura> coberturas = coberturaRepository.findAll();

        // Assert
        assertThat(coberturas).allMatch(c -> Boolean.TRUE.equals(c.getEstado()));
    }

    @Test
    @DisplayName("Buscar cobertura por nombre existente debe retornar true")
    void existeCoberturaPorNombre_existente() {
        // Arrange
        Cobertura cobertura = Cobertura.builder()
                .nombre("Cobertura Plus")
                .descripcion("Mayor protección")
                .estado(true)
                .build();
        coberturaRepository.save(cobertura);
        entityManager.flush();
        entityManager.clear();

        // Act
        boolean existe = coberturaRepository.existsByNombre("Cobertura Plus");

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Buscar cobertura por nombre inexistente debe retornar false")
    void existeCoberturaPorNombre_inexistente() {
        // Act
        boolean existe = coberturaRepository.existsByNombre("NoExiste");

        // Assert
        assertThat(existe).isFalse();
    }
}
