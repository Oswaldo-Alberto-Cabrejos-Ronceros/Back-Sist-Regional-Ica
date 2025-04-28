package com.clinicaregional.clinica.paciente.repository;

import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.enums.Sexo;
import com.clinicaregional.clinica.enums.TipoSangre;
import com.clinicaregional.clinica.repository.PacienteRepository;
import com.clinicaregional.clinica.repository.TipoDocumentoRepository;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PacienteRepositoryTest {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private TipoDocumento tipoDocumento;

    @BeforeEach
    void setUp() {
        // Crear tipo de documento válido antes de cada test
        tipoDocumento = new TipoDocumento();
        tipoDocumento.setNombre("DNI");
        tipoDocumento = tipoDocumentoRepository.save(tipoDocumento);

        // Activar filtro de estado (opcional si usas filtros)
        Session session = entityManager.getEntityManager().unwrap(Session.class);
        Filter filter = session.enableFilter("estadoActivo");
        filter.setParameter("estado", true);
    }

    @Test
    @DisplayName("Guardar y buscar paciente por ID y estado TRUE")
    void findByIdAndEstadoIsTrue() {
        // Arrange
        Paciente paciente = Paciente.builder()
                .nombres("Diego")
                .apellidos("Aguilar")
                .fechaNacimiento(LocalDate.of(2000, 5, 20))
                .sexo(Sexo.MASCULINO)
                .tipoDocumento(tipoDocumento)
                .numeroIdentificacion("12345678")
                .nacionalidad("PERUANA")
                .telefono("987654321")
                .direccion("Av. Siempre Viva 742")
                .tipoSangre(TipoSangre.O_POSITIVO)
                .estado(true)
                .build();
        paciente = pacienteRepository.save(paciente);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<Paciente> encontrado = pacienteRepository.findByIdAndEstadoIsTrue(paciente.getId());

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombres()).isEqualTo("Diego");
    }

    @Test
    @DisplayName("Buscar paciente por número de identificación")
    void findByNumeroIdentificacion() {
        // Arrange
        Paciente paciente = Paciente.builder()
                .nombres("Lucía")
                .apellidos("Gómez")
                .fechaNacimiento(LocalDate.of(1998, 8, 15))
                .sexo(Sexo.FEMENINO)
                .tipoDocumento(tipoDocumento)
                .numeroIdentificacion("87654321")
                .nacionalidad("PERUANA")
                .telefono("912345678")
                .direccion("Jr. Las Flores 123")
                .tipoSangre(TipoSangre.A_POSITIVO)
                .estado(true)
                .build();
        paciente = pacienteRepository.save(paciente);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<Paciente> encontrado = pacienteRepository.findByNumeroIdentificacion("87654321");

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getApellidos()).isEqualTo("Gómez");
    }
}
