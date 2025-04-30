package com.clinicaregional.clinica.recepcionista.repository;

import com.clinicaregional.clinica.entity.Recepcionista;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.enums.TurnoTrabajo;
import com.clinicaregional.clinica.repository.RecepcionistaRepository;
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
class RecepcionistaRepositoryTest {

    @Autowired
    private RecepcionistaRepository recepcionistaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Usuario usuario;
    private TipoDocumento tipoDocumento;
    private Recepcionista recepcionista;

    @BeforeEach
    void setup() {
        Session session = entityManager.getEntityManager().unwrap(Session.class);
        Filter filter = session.enableFilter("estadoActivo");
        filter.setParameter("estado", true);

        // Rol necesario
        Rol rol = new Rol();
        rol.setNombre("RECEPCIONISTA");
        rol.setDescripcion("Atiende pacientes");
        rol.setEstado(true);
        rol = entityManager.persist(rol);

        tipoDocumento = new TipoDocumento();
        tipoDocumento.setNombre("DNI");
        tipoDocumento.setEstado(true);
        tipoDocumento = entityManager.persist(tipoDocumento);

        usuario = new Usuario();
        usuario.setCorreo("recep@clinica.pe");
        usuario.setPassword("123456");
        usuario.setEstado(true);
        usuario.setRol(rol);
        usuario = entityManager.persist(usuario);

        recepcionista = Recepcionista.builder()
                .nombres("Ana")
                .apellidos("Perez")
                .numeroDocumento("12345678")
                .telefono("999999999")
                .direccion("Av. Salud")
                .turnoTrabajo(TurnoTrabajo.DIURNO)
                .fechaContratacion(LocalDate.now())
                .tipoDocumento(tipoDocumento)
                .usuario(usuario)
                .estado(true)
                .build();
        recepcionista = entityManager.persist(recepcionista);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Buscar por ID y estado true")
    void testFindByIdAndEstadoIsTrue() {
        Optional<Recepcionista> resultado = recepcionistaRepository.findByIdAndEstadoIsTrue(recepcionista.getId());
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombres()).isEqualTo("Ana");
    }

    @Test
    @DisplayName("Verificar existencia por número de documento")
    void testExistsByNumeroDocumento() {
        boolean existe = recepcionistaRepository.existsByNumeroDocumento("12345678");
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Verificar existencia por usuario")
    void testExistsByUsuario() {
        boolean existe = recepcionistaRepository.existsByUsuario(usuario);
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Buscar por ID de usuario")
    void testFindByUsuarioId() {
        Optional<Recepcionista> resultado = recepcionistaRepository.findByUsuario_Id(usuario.getId());
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getApellidos()).isEqualTo("Perez");
    }
}
