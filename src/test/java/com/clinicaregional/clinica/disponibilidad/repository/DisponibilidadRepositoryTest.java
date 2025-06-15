package com.clinicaregional.clinica.disponibilidad.repository;

import com.clinicaregional.clinica.entity.Disponibilidad;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.enums.DiaSemana;
import com.clinicaregional.clinica.enums.TipoContrato;
import com.clinicaregional.clinica.enums.TipoMedico;
import com.clinicaregional.clinica.repository.DisponibilidadRepository;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DisponibilidadRepositoryTest {

    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Medico medico;

    @BeforeEach
    void configurarFiltroYEntidadBase() {
        Session session = entityManager.getEntityManager().unwrap(Session.class);
        Filter filter = session.enableFilter("estadoActivo");
        filter.setParameter("estado", true);

        // Datos requeridos: Rol, TipoDocumento, Usuario, Medico
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
    }

    @Test
    @DisplayName("Guardar disponibilidad activa y buscar por ID")
    void guardarDisponibilidadActiva_debeEncontrarlaPorId() {
        // Arrange
        Disponibilidad disponibilidad = new Disponibilidad();
        disponibilidad.setDiaSemana(DiaSemana.MARTES);
        disponibilidad.setHoraInicio(LocalTime.of(9, 0));
        disponibilidad.setHoraFin(LocalTime.of(13, 0));
        disponibilidad.setNotas("Consulta general");
        disponibilidad.setMedico(medico);
        disponibilidad.setEstado(true);
        disponibilidad = disponibilidadRepository.save(disponibilidad);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<Disponibilidad> resultado = disponibilidadRepository.findByIdAndEstadoIsTrue(disponibilidad.getId());

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getDiaSemana()).isEqualTo(DiaSemana.MARTES);
    }

    @Test
    @DisplayName("Guardar disponibilidad inactiva y verificar que no se recupere")
    void guardarDisponibilidadInactiva_noDebeSerRecuperada() {
        // Arrange
        Disponibilidad disponibilidad = new Disponibilidad();
        disponibilidad.setDiaSemana(DiaSemana.JUEVES);
        disponibilidad.setHoraInicio(LocalTime.of(10, 0));
        disponibilidad.setHoraFin(LocalTime.of(12, 0));
        disponibilidad.setNotas("Inactiva");
        disponibilidad.setMedico(medico);
        disponibilidad.setEstado(false);
        disponibilidadRepository.save(disponibilidad);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<Disponibilidad> resultado = disponibilidadRepository.findByIdAndEstadoIsTrue(disponibilidad.getId());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Verificar existencia por médico y horario")
    void verificarExistenciaPorMedicoDiaYHorario() {
        // Arrange
        Disponibilidad disponibilidad = new Disponibilidad();
        disponibilidad.setDiaSemana(DiaSemana.LUNES);
        disponibilidad.setHoraInicio(LocalTime.of(8, 0));
        disponibilidad.setHoraFin(LocalTime.of(12, 0));
        disponibilidad.setNotas("Horario lunes");
        disponibilidad.setMedico(medico);
        disponibilidad.setEstado(true);
        disponibilidadRepository.save(disponibilidad);
        entityManager.flush();
        entityManager.clear();

        // Act
        boolean existe = disponibilidadRepository.existsByMedicoIdAndDiaSemanaAndHoraInicioAndHoraFin(
                medico.getId(), DiaSemana.LUNES, LocalTime.of(8, 0), LocalTime.of(12, 0));

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Contar disponibilidades activas por médico")
    void contarDisponibilidadesPorMedico() {
        // Arrange
        Disponibilidad d1 = crearDisponibilidad(DiaSemana.LUNES, true);
        Disponibilidad d2 = crearDisponibilidad(DiaSemana.MARTES, true);
        Disponibilidad d3 = crearDisponibilidad(DiaSemana.MIERCOLES, false); // inactiva

        disponibilidadRepository.saveAll(List.of(d1, d2, d3));
        entityManager.flush();
        entityManager.clear();

        // Act
        Long total = disponibilidadRepository.countByMedicoId(medico.getId());

        // Assert
        assertThat(total).isEqualTo(2);
    }

    @Test
    @DisplayName("Listar disponibilidades por ID de médico")
    void listarPorMedicoId_debeRetornarSoloActivos() {
        // Arrange
        crearDisponibilidad(DiaSemana.LUNES, true);
        crearDisponibilidad(DiaSemana.JUEVES, false); // inactiva
        entityManager.flush();
        entityManager.clear();

        // Act
        List<Disponibilidad> lista = disponibilidadRepository.findAllByMedicoId(medico.getId());

        // Assert
        assertThat(lista).allMatch(d -> d.getEstado().equals(true));
    }

    // Método auxiliar para reducir código duplicado
    private Disponibilidad crearDisponibilidad(DiaSemana dia, boolean estado) {
        Disponibilidad d = new Disponibilidad();
        d.setDiaSemana(dia);
        d.setHoraInicio(LocalTime.of(9, 0));
        d.setHoraFin(LocalTime.of(12, 0));
        d.setNotas("Bloque " + dia.name());
        d.setMedico(medico);
        d.setEstado(estado);
        return d;
    }
}
