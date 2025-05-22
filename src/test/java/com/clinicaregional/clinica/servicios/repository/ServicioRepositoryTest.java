package com.clinicaregional.clinica.servicios.repository;

import com.clinicaregional.clinica.entity.Servicio;
import com.clinicaregional.clinica.repository.ServicioRepository;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ServicioRepositoryTest {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void activarFiltroEstado() {
        Session session = entityManager.getEntityManager().unwrap(Session.class);
        Filter filter = session.enableFilter("estadoActivo");
        filter.setParameter("estado", true);
    }

    @Test
    @DisplayName("Guardar servicio con estado TRUE y encontrarlo por ID")
    void guardarServicio_conEstadoTrue_debeEncontrarloPorId() {
        // Arrange
        Servicio servicio = Servicio.builder()
                .nombre("Cardiología")
                .descripcion("Atención al corazón")
                .imagenUrl("cardiologia.jpg")
                .estado(true)
                .build();
        servicioRepository.save(servicio);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<Servicio> encontrado = servicioRepository.findByIdAndEstadoIsTrue(servicio.getId());

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Cardiología");
    }

    @Test
    @DisplayName("Guardar servicio con estado FALSE y no encontrarlo por ID")
    void guardarServicio_conEstadoFalse_noDebeEncontrarloPorId() {
        // Arrange
        Servicio servicio = Servicio.builder()
                .nombre("Traumatología")
                .descripcion("Especialidad de huesos")
                .imagenUrl("trauma.jpg")
                .estado(false)
                .build();
        servicioRepository.save(servicio);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<Servicio> encontrado = servicioRepository.findByIdAndEstadoIsTrue(servicio.getId());

        // Assert
        assertThat(encontrado).isEmpty();
    }

    @Test
    @DisplayName("Verificar si existe un servicio por nombre")
    void existeServicio_porNombre_debeRetornarTrue() {
        // Arrange
        Servicio servicio = Servicio.builder()
                .nombre("Pediatría")
                .descripcion("Salud infantil")
                .imagenUrl("pediatria.jpg")
                .estado(true)
                .build();
        servicioRepository.save(servicio);
        entityManager.flush();
        entityManager.clear();

        // Act
        boolean existe = servicioRepository.existsByNombre("Pediatría");

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Verificar que no existe un servicio por nombre inexistente")
    void noExisteServicio_porNombre_debeRetornarFalse() {
        // Act
        boolean existe = servicioRepository.existsByNombre("Odontología");

        // Assert
        assertThat(existe).isFalse();
    }
}
