package com.clinicaregional.clinica.seguro.repository;

import com.clinicaregional.clinica.entity.Seguro;
import com.clinicaregional.clinica.enums.EstadoSeguro;
import com.clinicaregional.clinica.repository.SeguroRepository;
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
class SeguroRepositoryTest {

    @Autowired
    private SeguroRepository seguroRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void activarFiltroEstado() {
        Session session = entityManager.getEntityManager().unwrap(Session.class);
        Filter filter = session.enableFilter("estadoActivo");
        filter.setParameter("estado", true);
    }

    @Test
    @DisplayName("Guardar seguro con estado TRUE y encontrar por ID")
    void guardarSeguro_conEstadoTrue_debeEncontrarsePorId() {
        // Arrange
        Seguro seguro = Seguro.builder()
                .nombre("RIMAC")
                .descripcion("Cobertura nacional")
                .imagenUrl("rimac.jpg")
                .estadoSeguro(EstadoSeguro.ACTIVO)
                .estado(true)
                .build();
        seguroRepository.save(seguro);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<Seguro> encontrado = seguroRepository.findByIdAndEstadoIsTrue(seguro.getId());

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("RIMAC");
    }

    @Test
    @DisplayName("Guardar seguro con estado FALSE y no encontrar por ID")
    void guardarSeguro_conEstadoFalse_noDebeEncontrarsePorId() {
        // Arrange
        Seguro seguro = Seguro.builder()
                .nombre("MAPFRE")
                .descripcion("Seguro internacional")
                .imagenUrl("mapfre.jpg")
                .estadoSeguro(EstadoSeguro.INACTIVO)
                .estado(false)
                .build();
        seguroRepository.save(seguro);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<Seguro> encontrado = seguroRepository.findByIdAndEstadoIsTrue(seguro.getId());

        // Assert
        assertThat(encontrado).isEmpty();
    }

    @Test
    @DisplayName("Listar seguros solo activos")
    void listarSeguros_debeRetornarSoloActivos() {
        // Arrange
        Seguro activo = Seguro.builder()
                .nombre("Pacífico")
                .descripcion("Salud privada")
                .imagenUrl("pacifico.jpg")
                .estadoSeguro(EstadoSeguro.ACTIVO)
                .estado(true)
                .build();

        Seguro inactivo = Seguro.builder()
                .nombre("Essalud")
                .descripcion("Seguro social")
                .imagenUrl("essalud.jpg")
                .estadoSeguro(EstadoSeguro.INACTIVO)
                .estado(false)
                .build();

        seguroRepository.save(activo);
        seguroRepository.save(inactivo);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<Seguro> seguros = seguroRepository.findAll();

        // Assert
        assertThat(seguros).allMatch(s -> Boolean.TRUE.equals(s.getEstado()));
    }

    @Test
    @DisplayName("Buscar seguro por nombre debe devolverlo si existe")
    void buscarSeguroPorNombre_existente_debeRetornarSeguro() {
        // Arrange
        Seguro seguro = Seguro.builder()
                .nombre("La Positiva")
                .descripcion("Seguro integral")
                .imagenUrl("positiva.jpg")
                .estadoSeguro(EstadoSeguro.ACTIVO)
                .estado(true)
                .build();

        seguroRepository.save(seguro);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<Seguro> resultado = seguroRepository.findByNombre("La Positiva");

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getDescripcion()).isEqualTo("Seguro integral");
    }

    @Test
    @DisplayName("Buscar seguro por nombre inexistente debe retornar vacío")
    void buscarSeguroPorNombre_inexistente_debeRetornarVacio() {
        // Act
        Optional<Seguro> resultado = seguroRepository.findByNombre("Inexistente");

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Verificar existencia por nombre")
    void existeSeguroPorNombre_debeRetornarTrueSiExiste() {
        // Arrange
        Seguro seguro = Seguro.builder()
                .nombre("SIS")
                .descripcion("Seguro público")
                .imagenUrl("sis.jpg")
                .estadoSeguro(EstadoSeguro.ACTIVO)
                .estado(true)
                .build();

        seguroRepository.save(seguro);
        entityManager.flush();
        entityManager.clear();

        // Act
        boolean existe = seguroRepository.existsByNombre("SIS");

        // Assert
        assertThat(existe).isTrue();
    }
}
