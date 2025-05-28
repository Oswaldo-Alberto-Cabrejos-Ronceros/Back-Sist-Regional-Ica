// package com.clinicaregional.clinica.medicoEspecialidad.repository;

// import com.clinicaregional.clinica.entity.*;
// import com.clinicaregional.clinica.enums.TipoContrato;
// import com.clinicaregional.clinica.enums.TipoMedico;
// import com.clinicaregional.clinica.repository.EspecialidadRepository;
// import com.clinicaregional.clinica.repository.MedicoEspecialidadRepository;
// import com.clinicaregional.clinica.repository.MedicoRepository;
// import com.clinicaregional.clinica.repository.UsuarioRepository;
// import org.hibernate.Filter;
// import org.hibernate.Session;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// import static org.assertj.core.api.Assertions.assertThat;

// @DataJpaTest
// class MedicoEspecialidadRepositoryTest {

//     @Autowired
//     private MedicoEspecialidadRepository medicoEspecialidadRepository;

//     @Autowired
//     private MedicoRepository medicoRepository;

//     @Autowired
//     private EspecialidadRepository especialidadRepository;

//     @Autowired
//     private UsuarioRepository usuarioRepository;

//     @Autowired
//     private TestEntityManager entityManager;

//     private Rol rol;

//     @BeforeEach
//     void setUp() {
//         Session session = entityManager.getEntityManager().unwrap(Session.class);
//         Filter filter = session.enableFilter("estadoActivo");
//         filter.setParameter("estado", true);

//         rol = Rol.builder()
//                 .nombre("ROLE_MEDICO")
//                 .descripcion("Rol para médicos")
//                 .estado(true)
//                 .build();
//         entityManager.persist(rol);
//     }

//     @Test
//     @DisplayName("Guardar relación médico-especialidad activa y buscar por ID")
//     void guardarRelacionMedicoEspecialidad_conEstadoTrue_debeEncontrarsePorId() {
//         // Arrange
//         Medico medico = crearMedico("Juan");
//         Especialidad especialidad = crearEspecialidad("Cardiología");

//         MedicoEspecialidad relacion = MedicoEspecialidad.builder()
//                 .id(new MedicoEspecialidadId(medico.getId(), especialidad.getId()))
//                 .medico(medico)
//                 .especialidad(especialidad)
//                 .desdeFecha(LocalDate.now())
//                 .estado(true)
//                 .build();

//         entityManager.persistAndFlush(relacion);

//         // Act
//         Optional<MedicoEspecialidad> resultado = medicoEspecialidadRepository.findByIdAndEstadoIsTrue(relacion.getId());

//         // Assert
//         assertThat(resultado).isPresent();
//         assertThat(resultado.get().getMedico().getNombres()).isEqualTo("Juan");
//         assertThat(resultado.get().getEspecialidad().getNombre()).isEqualTo("Cardiología");
//     }

//     @Test
//     @DisplayName("Verificar existencia de relación médico-especialidad activa")
//     void existsByMedicoAndEspecialidad_debeRetornarTrueSiExiste() {
//         // Arrange
//         Medico medico = crearMedico("Pedro");
//         Especialidad especialidad = crearEspecialidad("Dermatología");

//         MedicoEspecialidad relacion = MedicoEspecialidad.builder()
//                 .id(new MedicoEspecialidadId(medico.getId(), especialidad.getId()))
//                 .medico(medico)
//                 .especialidad(especialidad)
//                 .desdeFecha(LocalDate.now())
//                 .estado(true)
//                 .build();

//         entityManager.persistAndFlush(relacion);

//         // Act
//         boolean existe = medicoEspecialidadRepository.existsByMedicoAndEspecialidad(medico, especialidad);

//         // Assert
//         assertThat(existe).isTrue();
//     }

//     @Test
//     @DisplayName("Buscar todas las especialidades de un médico")
//     void findByMedicoId_debeRetornarListaDeEspecialidades() {
//         // Arrange
//         Medico medico = crearMedico("Luis");
//         Especialidad especialidad1 = crearEspecialidad("Neurología");
//         Especialidad especialidad2 = crearEspecialidad("Pediatría");

//         entityManager.persistAndFlush(
//                 MedicoEspecialidad.builder()
//                         .id(new MedicoEspecialidadId(medico.getId(), especialidad1.getId()))
//                         .medico(medico)
//                         .especialidad(especialidad1)
//                         .desdeFecha(LocalDate.now())
//                         .estado(true)
//                         .build());

//         entityManager.persistAndFlush(
//                 MedicoEspecialidad.builder()
//                         .id(new MedicoEspecialidadId(medico.getId(), especialidad2.getId()))
//                         .medico(medico)
//                         .especialidad(especialidad2)
//                         .desdeFecha(LocalDate.now())
//                         .estado(true)
//                         .build());

//         // Act
//         List<MedicoEspecialidad> relaciones = medicoEspecialidadRepository.findByMedicoId(medico.getId());

//         // Assert
//         assertThat(relaciones).hasSize(2);
//     }

//     @Test
//     @DisplayName("Buscar todos los médicos de una especialidad")
//     void findByEspecialidadId_debeRetornarListaDeMedicos() {
//         // Arrange
//         Especialidad especialidad = crearEspecialidad("Ginecología");
//         Medico medico1 = crearMedico("Ana");
//         Medico medico2 = crearMedico("Carlos");

//         entityManager.persistAndFlush(
//                 MedicoEspecialidad.builder()
//                         .id(new MedicoEspecialidadId(medico1.getId(), especialidad.getId()))
//                         .medico(medico1)
//                         .especialidad(especialidad)
//                         .desdeFecha(LocalDate.now())
//                         .estado(true)
//                         .build());

//         entityManager.persistAndFlush(
//                 MedicoEspecialidad.builder()
//                         .id(new MedicoEspecialidadId(medico2.getId(), especialidad.getId()))
//                         .medico(medico2)
//                         .especialidad(especialidad)
//                         .desdeFecha(LocalDate.now())
//                         .estado(true)
//                         .build());

//         // Act
//         List<MedicoEspecialidad> relaciones = medicoEspecialidadRepository.findByEspecialidadId(especialidad.getId());

//         // Assert
//         assertThat(relaciones).hasSize(2);
//     }

//     // Métodos auxiliares
//     private Medico crearMedico(String nombre) {
//         Usuario nuevoUsuario = Usuario.builder()
//                 .correo(nombre.toLowerCase() + "@gmail.com")
//                 .password("passwordSeguro123")
//                 .rol(rol)
//                 .estado(true)
//                 .build();
//         entityManager.persist(nuevoUsuario);

//         Medico medico = Medico.builder()
//                 .nombres(nombre)
//                 .apellidos("Pérez")
//                 .numeroColegiatura("12345678901")
//                 .numeroRNE("123456789")
//                 .telefono("987654321")
//                 .direccion("Av. Salud 123")
//                 .descripcion("Especialista en " + nombre)
//                 .imagen("foto.jpg")
//                 .fechaContratacion(LocalDateTime.now())
//                 .tipoContrato(TipoContrato.FIJO)
//                 .tipoMedico(TipoMedico.ESPECIALISTA)
//                 .usuario(nuevoUsuario)
//                 .estado(true)
//                 .build();
//         entityManager.persist(medico);
//         return medico;
//     }

//     private Especialidad crearEspecialidad(String nombre) {
//         Especialidad especialidad = Especialidad.builder()
//                 .nombre(nombre)
//                 .descripcion("Especialidad de " + nombre)
//                 .imagen("imagen_" + nombre.toLowerCase() + ".jpg")
//                 .estado(true)
//                 .build();
//         entityManager.persist(especialidad);
//         return especialidad;
//     }
// }
