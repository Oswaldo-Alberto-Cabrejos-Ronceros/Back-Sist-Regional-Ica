package com.clinicaregional.clinica.administrador.repository;

import com.clinicaregional.clinica.entity.Administrador;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.repository.AdministradorRepository;
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
class AdministradorRepositoryTest {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private TestEntityManager entityManager;

    private TipoDocumento tipo;
    private Usuario usuario;

    private Rol rol;

    @BeforeEach
    void setUp() {
        // Activar filtro
        Session session = entityManager.getEntityManager().unwrap(Session.class);
        Filter filter = session.enableFilter("estadoActivo");
        filter.setParameter("estado", true);

        // Crear y persistir Rol
        rol = Rol.builder()
                .nombre("ADMINISTRADOR")
                .descripcion("Rol admin")
                .estado(true)
                .build();
        entityManager.persist(rol);

        // Crear y persistir TipoDocumento
        tipo = TipoDocumento.builder()
                .nombre("DNI")
                .descripcion("Documento Nacional de Identidad")
                .estado(true)
                .build();
        entityManager.persist(tipo);

        // Crear y persistir Usuario con Rol asignado
        usuario = Usuario.builder()
                .correo("usuario@correo.com")
                .password("123456")
                .rol(rol)
                .estado(true)
                .build();
        entityManager.persist(usuario);
    }

    @Test
    @DisplayName("Guardar administrador con estado true debe ser recuperado")
    void guardarAdministrador_estadoTrue() {
        Administrador administrador = Administrador.builder()
                .nombres("Laura")
                .apellidos("Gómez")
                .numeroDocumento("87654321")
                .tipoDocumento(tipo)
                .telefono("912345678")
                .direccion("Av. Los Álamos 456")
                .fechaContratacion(LocalDate.now())
                .usuario(usuario)
                .estado(true)
                .build();

        administradorRepository.save(administrador);
        entityManager.flush();
        entityManager.clear();

        Optional<Administrador> resultado = administradorRepository.findByIdAndEstadoIsTrue(administrador.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombres()).isEqualTo("Laura");
    }

    @Test
    @DisplayName("Administrador con estado false no debe ser recuperado")
    void guardarAdministrador_estadoFalse() {
        Administrador administrador = Administrador.builder()
                .nombres("Carlos")
                .apellidos("Ruiz")
                .numeroDocumento("12312312")
                .tipoDocumento(tipo)
                .telefono("987654321")
                .direccion("Calle Luna 123")
                .fechaContratacion(LocalDate.now())
                .usuario(usuario)
                .estado(false)
                .build();

        administradorRepository.save(administrador);
        entityManager.flush();
        entityManager.clear();

        Optional<Administrador> resultado = administradorRepository.findByIdAndEstadoIsTrue(administrador.getId());

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Buscar por ID de usuario debe retornar administrador correcto")
    void buscarPorUsuarioId() {
        Administrador administrador = Administrador.builder()
                .nombres("Ana")
                .apellidos("Salas")
                .numeroDocumento("99999999")
                .tipoDocumento(tipo)
                .telefono("933112244")
                .direccion("Jr. Lima 789")
                .fechaContratacion(LocalDate.now())
                .usuario(usuario)
                .estado(true)
                .build();

        administradorRepository.save(administrador);
        entityManager.flush();
        entityManager.clear();

        Optional<Administrador> resultado = administradorRepository.findByUsuario_Id(usuario.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombres()).isEqualTo("Ana");
    }

    @Test
    @DisplayName("Verificar existencia por número de documento")
    void existePorNumeroDocumento() {
        Administrador administrador = Administrador.builder()
                .nombres("Mario")
                .apellidos("Castro")
                .numeroDocumento("10101010")
                .tipoDocumento(tipo)
                .telefono("999111222")
                .direccion("Calle Real 101")
                .fechaContratacion(LocalDate.now())
                .usuario(usuario)
                .estado(true)
                .build();

        administradorRepository.save(administrador);
        entityManager.flush();
        entityManager.clear();

        boolean existe = administradorRepository.existsByNumeroDocumento("10101010");

        assertThat(existe).isTrue();
    }
}
