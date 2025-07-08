package com.clinicaregional.clinica.cita.repository;

import com.clinicaregional.clinica.entity.*;
import com.clinicaregional.clinica.enums.EstadoCita;
import com.clinicaregional.clinica.enums.Sexo;
import com.clinicaregional.clinica.enums.TipoContrato;
import com.clinicaregional.clinica.enums.TipoMedico;
import com.clinicaregional.clinica.enums.TipoSangre;
import com.clinicaregional.clinica.repository.CitaRepository;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CitaRepositoryTest {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Medico medico;
    private Paciente paciente;

    @BeforeEach
    void configurarFiltroYEntidadesBase() {
        // Activar filtro de estado lógico
        Session session = entityManager.getEntityManager().unwrap(Session.class);
        Filter filter = session.enableFilter("estadoActivo");
        filter.setParameter("estado", true);

        Rol rol = new Rol();
        rol.setNombre("PACIENTE");
        rol.setDescripcion("Rol de prueba");
        rol.setEstado(true);
        rol = entityManager.persist(rol);

        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setNombre("DNI");
        tipoDocumento.setEstado(true);
        tipoDocumento = entityManager.persist(tipoDocumento);

        Usuario userPaciente = new Usuario();
        userPaciente.setCorreo("paciente@clinica.pe");
        userPaciente.setPassword("123456");
        userPaciente.setRol(rol);
        userPaciente.setEstado(true);
        userPaciente = entityManager.persist(userPaciente);

        Usuario userMedico = new Usuario();
        userMedico.setCorreo("medico@clinica.pe");
        userMedico.setPassword("clave123");
        userMedico.setRol(rol);
        userMedico.setEstado(true);
        userMedico = entityManager.persist(userMedico);

        paciente = crearPaciente(userPaciente, tipoDocumento);
        medico = crearMedico(userMedico, tipoDocumento);
    }

    @Test
    @DisplayName("Buscar pacientes con citas confirmadas o atendidas por médico")
    void buscarPacientesPorMedicoYEstadoCita() {
        // Arrange
        crearCita(EstadoCita.CONFIRMADA);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<Paciente> pacientes = citaRepository.findPacientesByMedicoIdAndEstadoCitaIn(
                medico.getId(), List.of(EstadoCita.CONFIRMADA, EstadoCita.ATENDIDA));

        // Assert
        assertThat(pacientes).hasSize(1);
        assertThat(pacientes.get(0).getId()).isEqualTo(paciente.getId());
    }

    @Test
    @DisplayName("Buscar citas por lista de estados (CONFIRMADA, ATENDIDA)")
    void buscarCitasPorEstados() {
        // Arrange
        crearCita(EstadoCita.CONFIRMADA);
        crearCita(EstadoCita.ATENDIDA);
        crearCita(EstadoCita.CANCELADA);

        entityManager.flush();
        entityManager.clear();

        // Act
        List<Cita> citas = citaRepository.findByEstadoCitaIn(
                List.of(EstadoCita.CONFIRMADA, EstadoCita.ATENDIDA));

        // Assert
        assertThat(citas).hasSize(2);
        assertThat(citas)
                .allMatch(c -> c.getEstadoCita() == EstadoCita.CONFIRMADA || c.getEstadoCita() == EstadoCita.ATENDIDA);
    }

    private Paciente crearPaciente(Usuario usuario, TipoDocumento tipoDocumento) {
        Paciente p = Paciente.builder()
                .nombres("Carlos")
                .apellidos("Ramirez")
                .sexo(Sexo.MASCULINO)
                .fechaNacimiento(LocalDate.of(1995, 5, 12))
                .tipoDocumento(tipoDocumento)
                .numeroIdentificacion("12345678")
                .telefono("987654321")
                .direccion("Av. Salud 123")
                .tipoSangre(TipoSangre.O_POSITIVO)
                .usuario(usuario)
                .estado(true)
                .build();
        return entityManager.persist(p);
    }

    private Medico crearMedico(Usuario usuario, TipoDocumento tipoDocumento) {
        Medico m = Medico.builder()
                .nombres("Luis")
                .apellidos("Gonzales")
                .numeroColegiatura("123456789")
                .numeroRNE("987654321")
                .numeroDocumento("87654321")
                .telefono("987654321")
                .direccion("Av. Salud 456")
                .descripcion("Especialista")
                .fechaContratacion(LocalDate.now().atStartOfDay())
                .tipoContrato(TipoContrato.FIJO)
                .tipoMedico(TipoMedico.ESPECIALISTA)
                .tipoDocumento(tipoDocumento)
                .usuario(usuario)
                .estado(true)
                .build();
        return entityManager.persist(m);
    }

    private void crearCita(EstadoCita estado) {
        Cita cita = new Cita();
        cita.setFecha(LocalDate.now());
        cita.setHora(LocalTime.of(10, 0));
        cita.setEstadoCita(estado);
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        entityManager.persist(cita);
    }
}
