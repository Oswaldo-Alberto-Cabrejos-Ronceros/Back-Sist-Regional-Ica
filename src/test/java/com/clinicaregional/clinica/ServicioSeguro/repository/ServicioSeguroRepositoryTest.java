package com.clinicaregional.clinica.ServicioSeguro.repository;

import com.clinicaregional.clinica.entity.Cobertura;
import com.clinicaregional.clinica.entity.Seguro;
import com.clinicaregional.clinica.entity.Servicio;
import com.clinicaregional.clinica.entity.ServicioSeguro;
import com.clinicaregional.clinica.repository.ServicioSeguroRepository;
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
class ServicioSeguroRepositoryTest {

    @Autowired
    private ServicioSeguroRepository servicioSeguroRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Servicio servicio;
    private Seguro seguro;
    private Cobertura cobertura;

    @BeforeEach
    void setUp() {
        Session session = entityManager.getEntityManager().unwrap(Session.class);
        Filter filter = session.enableFilter("estadoActivo");
        filter.setParameter("estado", true);

        servicio = Servicio.builder()
                .nombre("Cardiología")
                .descripcion("Atiende enfermedades del corazón")
                .imagenUrl("cardio.jpg")
                .estado(true)
                .build();

        seguro = Seguro.builder()
                .nombre("RIMAC")
                .descripcion("Cobertura nacional")
                .estado(true)
                .build();

        cobertura = Cobertura.builder()
                .nombre("Cobertura Total")
                .descripcion("Incluye todo")
                .estado(true)
                .build();

        entityManager.persistAndFlush(servicio);
        entityManager.persistAndFlush(seguro);
        entityManager.persistAndFlush(cobertura);
    }

    @Test
    @DisplayName("Guardar ServicioSeguro con estado TRUE y buscar por ID")
    void guardarServicioSeguro_conEstadoTrue_debeEncontrarsePorId() {
        // Arrange
        ServicioSeguro ss = ServicioSeguro.builder()
                .servicio(servicio)
                .seguro(seguro)
                .cobertura(cobertura)
                .estado(true)
                .build();
        entityManager.persistAndFlush(ss);

        // Act
        Optional<ServicioSeguro> encontrado = servicioSeguroRepository.findByIdAndEstadoIsTrue(ss.getId());

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getServicio().getNombre()).isEqualTo("Cardiología");
    }

    @Test
    @DisplayName("Guardar ServicioSeguro con estado FALSE y verificar que no se encuentre por ID")
    void guardarServicioSeguro_conEstadoFalse_noDebeEncontrarsePorId() {
        // Arrange
        ServicioSeguro ss = ServicioSeguro.builder()
                .servicio(servicio)
                .seguro(seguro)
                .cobertura(cobertura)
                .estado(false)
                .build();
        entityManager.persistAndFlush(ss);

        // Act
        Optional<ServicioSeguro> encontrado = servicioSeguroRepository.findByIdAndEstadoIsTrue(ss.getId());

        // Assert
        assertThat(encontrado).isEmpty();
    }

    @Test
    @DisplayName("Buscar por servicioId debe retornar coincidencias")
    void findByServicioId_debeRetornarLista() {
        // Arrange
        ServicioSeguro ss = ServicioSeguro.builder()
                .servicio(servicio)
                .seguro(seguro)
                .cobertura(cobertura)
                .estado(true)
                .build();
        entityManager.persistAndFlush(ss);

        // Act
        List<ServicioSeguro> resultado = servicioSeguroRepository.findByServicio_Id(servicio.getId());

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getSeguro().getNombre()).isEqualTo("RIMAC");
    }

    @Test
    @DisplayName("Buscar por seguroId debe retornar coincidencias")
    void findBySeguroId_debeRetornarLista() {
        // Arrange
        ServicioSeguro ss = ServicioSeguro.builder()
                .servicio(servicio)
                .seguro(seguro)
                .cobertura(cobertura)
                .estado(true)
                .build();
        entityManager.persistAndFlush(ss);

        // Act
        List<ServicioSeguro> resultado = servicioSeguroRepository.findBySeguro_Id(seguro.getId());

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getServicio().getNombre()).isEqualTo("Cardiología");
    }

    @Test
    @DisplayName("Buscar por coberturaId debe retornar coincidencias")
    void findByCoberturaId_debeRetornarLista() {
        // Arrange
        ServicioSeguro ss = ServicioSeguro.builder()
                .servicio(servicio)
                .seguro(seguro)
                .cobertura(cobertura)
                .estado(true)
                .build();
        entityManager.persistAndFlush(ss);

        // Act
        List<ServicioSeguro> resultado = servicioSeguroRepository.findByCobertura_Id(cobertura.getId());

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getSeguro().getNombre()).isEqualTo("RIMAC");
    }

    @Test
    @DisplayName("Validar existencia por servicioId, seguroId y coberturaId")
    void existsByServicioSeguroCobertura_debeRetornarTrueSiExiste() {
        // Arrange
        ServicioSeguro ss = ServicioSeguro.builder()
                .servicio(servicio)
                .seguro(seguro)
                .cobertura(cobertura)
                .estado(true)
                .build();
        entityManager.persistAndFlush(ss);

        // Act
        boolean existe = servicioSeguroRepository.existsByServicio_IdAndSeguro_IdAndCobertura_Id(
                servicio.getId(), seguro.getId(), cobertura.getId()
        );

        // Assert
        assertThat(existe).isTrue();
    }
}
