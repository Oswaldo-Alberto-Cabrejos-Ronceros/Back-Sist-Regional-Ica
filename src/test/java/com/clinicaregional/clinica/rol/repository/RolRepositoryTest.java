package com.clinicaregional.clinica.rol.repository;

import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.repository.RolRepository;
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
class RolRepositoryTest {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void activarFiltroEstado() {
        Session session = entityManager.getEntityManager().unwrap(Session.class);
        Filter filter = session.enableFilter("estadoActivo");
        filter.setParameter("estado", true);
    }

    @Test
    @DisplayName("Guardar rol con estado true debe permitir encontrarlo por nombre y estado activo")
    void guardarRol_conEstadoTrue_debeEncontrarloPorNombreYEstadoTrue() {
        Rol rol = new Rol();
        rol.setNombre("ADMIN");
        rol.setDescripcion("Administrador del sistema");
        rol.setEstado(true);
        rolRepository.save(rol);
        entityManager.flush();
        entityManager.clear();

        boolean existe = rolRepository.existsByNombreAndEstadoIsTrue("ADMIN");

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Guardar rol con estado false debe encontrarlo pero con estado false")
    void guardarRol_conEstadoFalse_debeEncontrarloPorNombrePeroConEstadoFalse() {
        // Arrange
        Rol rol = new Rol();
        rol.setNombre("PACIENTE");
        rol.setDescripcion("Paciente registrado");
        rol.setEstado(false);
        Rol rolGuardado = rolRepository.save(rol);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<Rol> resultado = rolRepository.findById(rolGuardado.getId());

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEstado()).isFalse();
    }

    @Test
    @DisplayName("Guardar rol y buscar por ID y estado true debe encontrar el rol")
    void guardarRol_conEstadoTrue_debeEncontrarloPorIdYEstadoTrue() {
        Rol rol = new Rol();
        rol.setNombre("RECEPCIONISTA");
        rol.setDescripcion("Recepcionista de la clínica");
        rol.setEstado(true);
        Rol rolGuardado = rolRepository.save(rol);
        entityManager.flush();
        entityManager.clear();

        Optional<Rol> resultado = rolRepository.findByIdAndEstadoIsTrue(rolGuardado.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("RECEPCIONISTA");
    }
}
