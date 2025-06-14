package com.clinicaregional.clinica.disponibilidad.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.util.Optional;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.clinicaregional.clinica.entity.Disponibilidad;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.enums.DiaSemana;
import com.clinicaregional.clinica.enums.TipoContrato;
import com.clinicaregional.clinica.enums.TipoMedico;
import com.clinicaregional.clinica.repository.DisponibilidadRepository;

@DataJpaTest
class DisponibilidadRepositoryTest {

    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Medico medico;
    private Disponibilidad disponibilidad;
    private TipoDocumento tipoDocumento;

    @BeforeEach
    void setUp() {
        Session session = entityManager.getEntityManager().unwrap(Session.class);
        Filter filter = session.enableFilter("estadoActivo");
        filter.setParameter("estado", true);

        Rol rol = new Rol();
        rol.setNombre("MEDICO");
        rol.setDescripcion("Médico general");
        rol.setEstado(true);
        rol = entityManager.persist(rol);

        tipoDocumento = new TipoDocumento();
        tipoDocumento.setNombre("DNI");
        tipoDocumento.setEstado(true);
        tipoDocumento = entityManager.persist(tipoDocumento);

        Usuario usuario = new Usuario();
        usuario.setCorreo("medico@clinica.pe");
        usuario.setPassword("medID123");
        usuario.setEstado(true);
        usuario.setRol(rol);
        usuario = entityManager.persist(usuario);

        medico = Medico.builder()
                .nombres("Juan")
                .apellidos("Perez")
                .numeroColegiatura("123456")
                .numeroRNE("654321")
                .tipoDocumento(tipoDocumento)
                .numeroDocumento("12345678")
                .telefono("999999999")
                .direccion("Calle Salud 123")
                .descripcion("Médico con experiencia")
                .imagen("https://img.jpg")
                .fechaContratacion(java.time.LocalDateTime.now())
                .tipoContrato(TipoContrato.FIJO)
                .tipoMedico(TipoMedico.GENERAL)
                .usuario(usuario)
                .estado(true)
                .build();
        medico = entityManager.persist(medico);

        disponibilidad = new Disponibilidad();
        disponibilidad.setDiaSemana(DiaSemana.LUNES);
        disponibilidad.setHoraInicio(LocalTime.of(8, 0));
        disponibilidad.setHoraFin(LocalTime.of(14, 0));
        disponibilidad.setNotas("Atención general");
        disponibilidad.setMedico(medico);
        disponibilidad.setEstado(true);
        disponibilidad = entityManager.persist(disponibilidad);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Buscar por ID y estado true")
    void testFindByIdAndEstadoIsTrue() {
        Optional<Disponibilidad> resultado = disponibilidadRepository.findByIdAndEstadoIsTrue(disponibilidad.getId());
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getDiaSemana()).isEqualTo(DiaSemana.LUNES);
    }

    @Test
    @DisplayName("Verificar existencia de disponibilidad por médico, día y hora")
    void testExistsByMedicoIdAndDiaSemanaAndHoraInicioAndHoraFin() {
        boolean existe = disponibilidadRepository.existsByMedicoIdAndDiaSemanaAndHoraInicioAndHoraFin(
                medico.getId(),
                DiaSemana.LUNES,
                LocalTime.of(8, 0),
                LocalTime.of(14, 0));
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Listar disponibilidades por médico")
    void testFindAllByMedicoId() {
        var lista = disponibilidadRepository.findAllByMedicoId(medico.getId());
        assertThat(lista).isNotEmpty();
        assertThat(lista.get(0).getNotas()).isEqualTo("Atención general");
    }

    @Test
    @DisplayName("Verificar existencia por ID de médico")
    void testExistsByMedicoId() {
        boolean existe = disponibilidadRepository.existsByMedicoId(medico.getId());
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Contar disponibilidades por médico")
    void testCountByMedicoId() {
        Long total = disponibilidadRepository.countByMedicoId(medico.getId());
        assertThat(total).isEqualTo(1);
    }

}
