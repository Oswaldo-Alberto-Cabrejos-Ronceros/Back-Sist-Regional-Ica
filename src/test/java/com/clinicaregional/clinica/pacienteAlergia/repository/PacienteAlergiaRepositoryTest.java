package com.clinicaregional.clinica.pacienteAlergia.repository;

import com.clinicaregional.clinica.entity.Alergia;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.entity.PacienteAlergia;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.enums.TipoAlergia;
import com.clinicaregional.clinica.repository.PacienteAlergiaRepository;
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
class PacienteAlergiaRepositoryTest {

    @Autowired
    private PacienteAlergiaRepository pacienteAlergiaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private TipoDocumento tipoDocumento;

    @BeforeEach
    void setUp() {
        Session session = entityManager.getEntityManager().unwrap(Session.class);
        Filter filter = session.enableFilter("estadoActivo");
        filter.setParameter("estado", true);

        // Crear un tipo de documento válido para los pacientes
        tipoDocumento = new TipoDocumento();
        tipoDocumento.setNombre("DNI");
        entityManager.persist(tipoDocumento);
    }

    @Test
    @DisplayName("Guardar paciente-alergia con estado true y buscar por ID")
    void guardarPacienteAlergia_conEstadoTrue_debeEncontrarsePorId() {
        // Arrange
        Paciente paciente = new Paciente();
        paciente.setNombres("Juan Perez");
        paciente.setTipoDocumento(tipoDocumento); // 👈 ahora sí
        entityManager.persist(paciente);

        Alergia alergia = new Alergia();
        alergia.setNombre("Polen");
        alergia.setTipoAlergia(TipoAlergia.AMBIENTAL);
        entityManager.persist(alergia);

        PacienteAlergia pacienteAlergia = new PacienteAlergia();
        pacienteAlergia.setPaciente(paciente);
        pacienteAlergia.setAlergia(alergia);
        pacienteAlergia.setEstado(true);
        entityManager.persistAndFlush(pacienteAlergia);

        // Act
        Optional<PacienteAlergia> resultado = pacienteAlergiaRepository
                .findByIdAndEstadoIsTrue(pacienteAlergia.getId());

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getPaciente().getNombres()).isEqualTo("Juan Perez");
        assertThat(resultado.get().getAlergia().getNombre()).isEqualTo("Polen");
    }

    @Test
    @DisplayName("Verificar existencia de relación paciente-alergia")
    void existsByPacienteAndAlergia_debeRetornarTrueSiExiste() {
        // Arrange
        Paciente paciente = new Paciente();
        paciente.setNombres("Ana Lopez");
        paciente.setTipoDocumento(tipoDocumento); // 👈 ahora sí
        entityManager.persist(paciente);

        Alergia alergia = new Alergia();
        alergia.setNombre("Maní");
        alergia.setTipoAlergia(TipoAlergia.ALIMENTARIA);
        entityManager.persist(alergia);

        PacienteAlergia pacienteAlergia = new PacienteAlergia();
        pacienteAlergia.setPaciente(paciente);
        pacienteAlergia.setAlergia(alergia);
        pacienteAlergia.setEstado(true);
        entityManager.persistAndFlush(pacienteAlergia);

        // Act
        boolean existe = pacienteAlergiaRepository.existsByPacienteAndAlergia(paciente, alergia);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Buscar todas las alergias de un paciente")
    void findByPaciente_debeRetornarListaDeAlergias() {
        // Arrange
        Paciente paciente = new Paciente();
        paciente.setNombres("Luis Gonzalez");
        paciente.setTipoDocumento(tipoDocumento); // 👈 ahora sí
        entityManager.persist(paciente);

        Alergia alergia1 = new Alergia();
        alergia1.setNombre("Polvo");
        alergia1.setTipoAlergia(TipoAlergia.AMBIENTAL);
        entityManager.persist(alergia1);

        Alergia alergia2 = new Alergia();
        alergia2.setNombre("Lactosa");
        alergia2.setTipoAlergia(TipoAlergia.ALIMENTARIA);
        entityManager.persist(alergia2);

        PacienteAlergia pa1 = new PacienteAlergia();
        pa1.setPaciente(paciente);
        pa1.setAlergia(alergia1);
        pa1.setEstado(true);

        PacienteAlergia pa2 = new PacienteAlergia();
        pa2.setPaciente(paciente);
        pa2.setAlergia(alergia2);
        pa2.setEstado(true);

        entityManager.persist(pa1);
        entityManager.persist(pa2);
        entityManager.flush();

        // Act
        List<PacienteAlergia> resultado = pacienteAlergiaRepository.findByPaciente(paciente);

        // Assert
        assertThat(resultado).hasSize(2);
    }
}
