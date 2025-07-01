package com.clinicaregional.clinica.horario_bloque.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.clinicaregional.clinica.entity.Cita;
import com.clinicaregional.clinica.entity.Disponibilidad;
import com.clinicaregional.clinica.entity.HorarioBloque;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.enums.EstadoBloque;
import com.clinicaregional.clinica.enums.TipoContrato;
import com.clinicaregional.clinica.enums.TipoMedico;
import com.clinicaregional.clinica.repository.HorarioBloqueRepository;

@DataJpaTest
class HorarioBloqueRepositoryTest {

    @Autowired
    private HorarioBloqueRepository horarioBloqueRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Medico medico;
    private Disponibilidad disponibilidad;
    private Cita cita;

    @BeforeEach
    void setUp() {
        Session session = entityManager.getEntityManager().unwrap(Session.class);
        Filter filter = session.enableFilter("estadoActivo");
        filter.setParameter("estado", true);

        Rol rol = new Rol();
        rol.setNombre("MEDICO");
        rol.setDescripcion("Rol médico");
        rol.setEstado(true);
        rol = entityManager.persist(rol);

        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setNombre("DNI");
        tipoDocumento.setEstado(true);
        tipoDocumento = entityManager.persist(tipoDocumento);

        Usuario usuario = new Usuario();
        usuario.setCorreo("medico@clinica.pe");
        usuario.setPassword("clave123");
        usuario.setEstado(true);
        usuario.setRol(rol);
        usuario = entityManager.persist(usuario);

        medico = Medico.builder()
                .nombres("Luis")
                .apellidos("Gonzales")
                .numeroColegiatura("123456789")
                .numeroRNE("987654321")
                .numeroDocumento("87654321")
                .telefono("987654321")
                .direccion("Av. Salud 456")
                .descripcion("Especialista")
                .imagen("https://img.jpg")
                .fechaContratacion(LocalDateTime.now())
                .tipoContrato(TipoContrato.FIJO)
                .tipoMedico(TipoMedico.GENERAL)
                .tipoDocumento(tipoDocumento)
                .usuario(usuario)
                .estado(true)
                .build();
        medico = entityManager.persist(medico);

        disponibilidad = Disponibilidad.builder()
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(12, 0))
                .notas("Consulta mañana")
                .medico(medico)
                .estado(true)
                .build();
        disponibilidad = entityManager.persist(disponibilidad);

        cita = new Cita();
        cita.setFecha(LocalDate.now());
        cita.setHora(LocalTime.of(10, 0));
        cita.setMedico(medico);
        cita = entityManager.persist(cita);
    }

    @Test
    @DisplayName("Test findByDisponibilidadId")
    void testFindByDisponibilidadId() {
        // Arrange
        HorarioBloque bloque = crearBloqueHoy();

        // Act
        List<HorarioBloque> bloques = horarioBloqueRepository.findByDisponibilidadId(disponibilidad.getId());

        // Assert
        assertThat(bloques).hasSize(1);
        assertThat(bloques.get(0).getId()).isEqualTo(bloque.getId());
    }

    @Test
    @DisplayName("Test findByDisponibilidad_Medico_Id")
    void testFindByMedicoId() {

        HorarioBloque bloque = crearBloqueHoy();
        List<HorarioBloque> bloques = horarioBloqueRepository.findByDisponibilidad_Medico_Id(medico.getId());

        // Assert

        assertThat(bloques).hasSize(1);
        assertThat(bloques.get(0).getId()).isEqualTo(bloque.getId());
    }

    @Test
    @DisplayName("Test findByFecha")
    void testFindByFecha() {
        // Arrange
        HorarioBloque bloque = crearBloqueHoy();

        // Act
        List<HorarioBloque> bloques = horarioBloqueRepository.findByFecha(LocalDate.now());

        // Assert
        assertThat(bloques).hasSize(1);
        assertThat(bloques.get(0).getId()).isEqualTo(bloque.getId());
        assertThat(bloques.get(0).getFecha()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Test findByFechaAndHoraInicioAndEstadoBloque")
    void testFindByFechaHoraEstado() {
        // Arrange
        HorarioBloque bloque = crearBloqueHoy();

        // Act
        Optional<HorarioBloque> resultado = horarioBloqueRepository.findByFechaAndHoraInicioAndEstadoBloque(
                bloque.getFecha(), bloque.getHoraInicio(), bloque.getEstadoBloque());

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getHoraFin()).isEqualTo(bloque.getHoraFin());
    }

    @Test
    @DisplayName("Test findByDisponibilidadIdAndFechaGreaterThanEqual")
    void testFindByDisponibilidadYFechaPosterior() {
        // Arrange
        crearBloqueHoy();

        // Act
        List<HorarioBloque> resultado = horarioBloqueRepository.findByDisponibilidadIdAndFechaGreaterThanEqual(
                disponibilidad.getId(), LocalDate.now());

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Test findByDisponibilidad_Medico_IdAndFechaGreaterThanEqual")
    void testFindByMedicoYFechaPosterior() {
        // Arrange
        crearBloqueHoy();

        // Act
        List<HorarioBloque> resultado = horarioBloqueRepository.findByDisponibilidad_Medico_IdAndFechaGreaterThanEqual(
                medico.getId(), LocalDate.now());

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Test findByCitaId")
    void testFindByCitaId() {
        // Arrange
        HorarioBloque bloque = crearBloqueHoy();
        bloque.setCita(cita);
        entityManager.persist(bloque);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<HorarioBloque> resultado = horarioBloqueRepository.findByCitaId(cita.getId());

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCita().getId()).isEqualTo(cita.getId());
    }

    @Test
    @DisplayName("Test findByCita")
    void testFindByCita() {
        // Arrange
        HorarioBloque bloque = crearBloqueHoy();
        bloque.setCita(cita);
        entityManager.persist(bloque);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<HorarioBloque> resultado = horarioBloqueRepository.findByCita(cita);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCita().getId()).isEqualTo(cita.getId());
    }

    // MÉTODO AUXILIAR
    private HorarioBloque crearBloqueHoy() {
        HorarioBloque bloque = HorarioBloque.builder()
                .fecha(LocalDate.now())
                .horaInicio(LocalTime.of(10, 0))
                .horaFin(LocalTime.of(10, 30))
                .estadoBloque(EstadoBloque.DISPONIBLE)
                .disponibilidad(disponibilidad)
                .build();
        return entityManager.persist(bloque);
    }
}
