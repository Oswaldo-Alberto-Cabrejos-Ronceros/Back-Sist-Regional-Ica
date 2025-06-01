package com.clinicaregional.clinica.medico.repository;

import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.enums.TipoContrato;
import com.clinicaregional.clinica.enums.TipoMedico;
import com.clinicaregional.clinica.repository.MedicoRepository;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MedicoRepositoryTest {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Usuario usuario;
    private TipoDocumento tipoDocumento;

    @BeforeEach
    void setup() {
        Session session = entityManager.getEntityManager().unwrap(Session.class);
        Filter filter = session.enableFilter("estadoActivo");
        filter.setParameter("estado", true);

        // Crear rol y usuario
        Rol rol = Rol.builder()
                .nombre("MEDICO")
                .descripcion("Rol médico")
                .estado(true)
                .build();
        entityManager.persist(rol);

        usuario = Usuario.builder()
                .correo("medico@correo.com")
                .password("securepass")
                .rol(rol)
                .estado(true)
                .build();
        entityManager.persist(usuario);

        // Crear tipo documento
        tipoDocumento = new TipoDocumento();
        tipoDocumento.setNombre("DNI");
        tipoDocumento.setEstado(true);
        entityManager.persist(tipoDocumento);
    }

    @Test
    @DisplayName("Debe guardar y encontrar médico activo por ID")
    void guardarYBuscarMedicoActivoPorId() {
        // Arrange
        Medico medico = Medico.builder()
                .nombres("Juan")
                .apellidos("Pérez")
                .numeroColegiatura("12345678901")
                .numeroRNE("987654321")
                .tipoDocumento(tipoDocumento)
                .numeroDocumento("70856984")
                .telefono("999888777")
                .direccion("Av. Salud 123")
                .descripcion("Médico general")
                .imagen("foto.png")
                .fechaContratacion(LocalDateTime.of(2023, 5, 20, 10, 30))
                .tipoContrato(TipoContrato.FIJO)
                .tipoMedico(TipoMedico.GENERAL)
                .usuario(usuario)
                .estado(true)
                .build();

        Medico guardado = medicoRepository.save(medico);

        // Act
        Optional<Medico> encontrado = medicoRepository.findByIdAndEstadoIsTrue(guardado.getId());

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNumeroColegiatura()).isEqualTo("12345678901");
    }

    @Test
    @DisplayName("No debe retornar médico si está inactivo")
    void noRetornarMedicoInactivo() {
        // Arrange
        Medico medico = Medico.builder()
                .nombres("Ana")
                .apellidos("Martínez")
                .numeroColegiatura("11122233344")
                .numeroRNE("999888777")
                .tipoDocumento(tipoDocumento)
                .numeroDocumento("70856985")
                .telefono("911222333")
                .direccion("Calle Salud 456")
                .descripcion("Especialista")
                .imagen("foto2.png")
                .fechaContratacion(LocalDateTime.now())
                .tipoContrato(TipoContrato.FIJO)
                .tipoMedico(TipoMedico.ESPECIALISTA)
                .usuario(usuario)
                .estado(false)
                .build();

        Medico guardado = medicoRepository.save(medico);

        // Act
        Optional<Medico> encontrado = medicoRepository.findByIdAndEstadoIsTrue(guardado.getId());

        // Assert
        assertThat(encontrado).isEmpty();
    }

    @Test
    @DisplayName("Debe verificar existencia por número de colegiatura")
    void existePorNumeroColegiatura() {
        // Arrange
        Medico medico = Medico.builder()
                .nombres("Pedro")
                .apellidos("Gonzales")
                .numeroColegiatura("12345678912")
                .numeroRNE("555666777")
                .tipoDocumento(tipoDocumento)
                .numeroDocumento("12345678")
                .telefono("999777888")
                .direccion("Pasaje Médico 789")
                .descripcion("Traumatólogo")
                .imagen("foto3.png")
                .fechaContratacion(LocalDateTime.now())
                .tipoContrato(TipoContrato.FIJO)
                .tipoMedico(TipoMedico.ESPECIALISTA)
                .usuario(usuario)
                .estado(true)
                .build();

        medicoRepository.save(medico);

        // Act
        boolean existe = medicoRepository.existsByNumeroColegiatura("12345678912");

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Debe encontrar médico por ID de usuario")
    void buscarMedicoPorUsuarioId() {
        // Arrange
        Medico medico = Medico.builder()
                .nombres("Luis")
                .apellidos("Vallejos")
                .numeroColegiatura("98765432100")
                .numeroRNE("111999888")
                .tipoDocumento(tipoDocumento)
                .numeroDocumento("98765432")
                .telefono("988776655")
                .direccion("Av. Central 456")
                .descripcion("Pediatra")
                .imagen("foto4.png")
                .fechaContratacion(LocalDateTime.now())
                .tipoContrato(TipoContrato.FIJO)
                .tipoMedico(TipoMedico.GENERAL)
                .usuario(usuario)
                .estado(true)
                .build();

        medicoRepository.save(medico);

        // Act
        Optional<Medico> encontrado = medicoRepository.findByUsuario_Id(usuario.getId());

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombres()).isEqualTo("Luis");
    }
}
