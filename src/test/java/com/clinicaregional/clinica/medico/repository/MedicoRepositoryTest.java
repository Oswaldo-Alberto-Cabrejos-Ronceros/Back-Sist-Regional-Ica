// package com.clinicaregional.clinica.medico.repository;

// import com.clinicaregional.clinica.entity.Medico;
// import com.clinicaregional.clinica.entity.Rol;
// import com.clinicaregional.clinica.entity.Usuario;
// import com.clinicaregional.clinica.enums.TipoContrato;
// import com.clinicaregional.clinica.enums.TipoMedico;
// import com.clinicaregional.clinica.repository.MedicoRepository;
// import com.clinicaregional.clinica.repository.RolRepository;
// import com.clinicaregional.clinica.repository.UsuarioRepository;
// import org.hibernate.Filter;
// import org.hibernate.Session;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// import static org.assertj.core.api.Assertions.assertThat;

// @DataJpaTest
// class MedicoRepositoryTest {

//     @Autowired
//     private MedicoRepository medicoRepository;

//     @Autowired
//     private UsuarioRepository usuarioRepository;

//     @Autowired
//     private RolRepository rolRepository;

//     @Autowired
//     private TestEntityManager entityManager;

//     private Usuario usuario;

//     @BeforeEach
//     void activarFiltroEstado() {
//         Session session = entityManager.getEntityManager().unwrap(Session.class);
//         Filter filter = session.enableFilter("estadoActivo");
//         filter.setParameter("estado", true);

//         // Crear un rol para el usuario
//         Rol rol = new Rol();
//         rol.setNombre("ROLE_MEDICO");
//         rol.setDescripcion("Rol médico para pruebas");
//         rol.setEstado(true);
//         entityManager.persist(rol);

//         usuario = new Usuario();
//         usuario.setCorreo("medico@example.com");
//         usuario.setPassword("passwordSeguro");
//         usuario.setEstado(true);
//         usuario.setRol(rol); // SOLUCION: asignar el rol
//         entityManager.persist(usuario);
//     }

//     @Test
//     @DisplayName("Guardar médico con estado activo y buscar por ID")
//     void guardarMedico_conEstadoTrue_debeEncontrarloPorId() {
//         // Arrange
//         Medico medico = crearMedico(true);
//         medicoRepository.save(medico);
//         entityManager.flush();
//         entityManager.clear();

//         // Act
//         Optional<Medico> encontrado = medicoRepository.findByIdAndEstadoIsTrue(medico.getId());

//         // Assert
//         assertThat(encontrado).isPresent();
//         assertThat(encontrado.get().getNombres()).isEqualTo("Juan");
//     }

//     @Test
//     @DisplayName("Guardar médico con estado inactivo y no encontrarlo por ID")
//     void guardarMedico_conEstadoFalse_noDebeEncontrarloPorId() {
//         // Arrange
//         Medico medico = crearMedico(false);
//         medicoRepository.save(medico);
//         entityManager.flush();
//         entityManager.clear();

//         // Act
//         Optional<Medico> encontrado = medicoRepository.findByIdAndEstadoIsTrue(medico.getId());

//         // Assert
//         assertThat(encontrado).isEmpty();
//     }

//     @Test
//     @DisplayName("Listar médicos debe retornar solo los activos")
//     void listarMedicos_debeRetornarSoloActivos() {
//         // Arrange
//         Medico activo = crearMedico(true);

//         // Crear otro usuario para el segundo médico
//         Usuario usuario2 = new Usuario();
//         usuario2.setCorreo("medico2@example.com");
//         usuario2.setPassword("passwordSeguro2");
//         usuario2.setRol(usuario.getRol()); // usa el mismo rol
//         usuario2.setEstado(true);
//         entityManager.persist(usuario2);

//         Medico inactivo = Medico.builder()
//                 .nombres("Pedro")
//                 .apellidos("Gonzalez")
//                 .numeroColegiatura("98765432101")
//                 .numeroRNE("987654321")
//                 .telefono("123456789")
//                 .direccion("Av. Secundaria 456")
//                 .descripcion("Médico general inactivo")
//                 .imagen("foto2.jpg")
//                 .fechaContratacion(LocalDateTime.now())
//                 .tipoContrato(TipoContrato.NOCTURNO)
//                 .tipoMedico(TipoMedico.PRACTICANTE)
//                 .usuario(usuario2)
//                 .estado(false)
//                 .build();

//         medicoRepository.save(activo);
//         medicoRepository.save(inactivo);
//         entityManager.flush();
//         entityManager.clear();

//         // Act
//         List<Medico> medicos = medicoRepository.findAll();

//         // Assert
//         assertThat(medicos).allMatch(m -> Boolean.TRUE.equals(m.getEstado()));
//     }

//     @Test
//     @DisplayName("Verificar existencia por número de colegiatura")
//     void existsByNumeroColegiatura() {
//         // Arrange
//         Medico medico = crearMedico(true);
//         medicoRepository.save(medico);
//         entityManager.flush();
//         entityManager.clear();

//         // Act
//         boolean existe = medicoRepository.existsByNumeroColegiatura(medico.getNumeroColegiatura());

//         // Assert
//         assertThat(existe).isTrue();
//     }

//     @Test
//     @DisplayName("Verificar existencia por número de RNE")
//     void existsByNumeroRNE() {
//         // Arrange
//         Medico medico = crearMedico(true);
//         medicoRepository.save(medico);
//         entityManager.flush();
//         entityManager.clear();

//         // Act
//         boolean existe = medicoRepository.existsByNumeroRNE(medico.getNumeroRNE());

//         // Assert
//         assertThat(existe).isTrue();
//     }

//     @Test
//     @DisplayName("Verificar existencia por usuario")
//     void existsByUsuario() {
//         // Arrange
//         Medico medico = crearMedico(true);
//         medicoRepository.save(medico);
//         entityManager.flush();
//         entityManager.clear();

//         // Act
//         boolean existe = medicoRepository.existsByUsuario(usuario);

//         // Assert
//         assertThat(existe).isTrue();
//     }

//     // Método auxiliar
//     private Medico crearMedico(boolean estado) {
//         return Medico.builder()
//                 .nombres("Juan")
//                 .apellidos("Pérez")
//                 .numeroColegiatura("12345678901")
//                 .numeroRNE("123456789")
//                 .telefono("987654321")
//                 .direccion("Av. Principal 123")
//                 .descripcion("Especialista en Medicina General")
//                 .imagen("foto.jpg")
//                 .fechaContratacion(LocalDateTime.now())
//                 .tipoContrato(TipoContrato.FIJO)
//                 .tipoMedico(TipoMedico.GENERAL)
//                 .usuario(usuario)
//                 .estado(estado)
//                 .build();
//     }
// }
