// package com.clinicaregional.clinica.disponibilidad.service;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.time.LocalTime;
// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;

// import com.clinicaregional.clinica.dto.request.DisponibilidadRequest;
// import com.clinicaregional.clinica.dto.response.DisponibilidadResponse;
// import com.clinicaregional.clinica.entity.Disponibilidad;
// import com.clinicaregional.clinica.entity.Medico;
// import com.clinicaregional.clinica.enums.DiaSemana;
// import com.clinicaregional.clinica.exception.ResourceNotFoundException;
// import com.clinicaregional.clinica.mapper.DisponibilidadMapper;
// import com.clinicaregional.clinica.repository.DisponibilidadRepository;
// import com.clinicaregional.clinica.repository.HorarioBloqueRepository;
// import com.clinicaregional.clinica.repository.MedicoRepository;
// import com.clinicaregional.clinica.service.impl.DisponibilidadServiceImpl;
// import com.clinicaregional.clinica.util.FiltroEstado;

// public class DisponibilidadServiceImplTest {

//         @Mock
//         private DisponibilidadRepository disponibilidadRepository;

//         @Mock
//         private DisponibilidadMapper disponibilidadMapper;

//         @Mock
//         private HorarioBloqueRepository horarioBloqueRepository;

//         @Mock
//         private MedicoRepository medicoRepository;

//         @Mock
//         private FiltroEstado filtroEstado;

//         @InjectMocks
//         private DisponibilidadServiceImpl disponibilidadService;

//         private Disponibilidad disponibilidad;
//         private Medico medico;
//         private DisponibilidadResponse response;

//         @BeforeEach
//         void setUp() {
//                 MockitoAnnotations.openMocks(this);

//                 medico = new Medico();
//                 medico.setId(1L);

//                 disponibilidad = Disponibilidad.builder()
//                                 .id(1L)
//                                 .diaSemana(DiaSemana.MIERCOLES)
//                                 .horaInicio(LocalTime.of(8, 0))
//                                 .horaFin(LocalTime.of(12, 0))
//                                 .notas("Citas del Dr. Aguirre")
//                                 .medico(medico)
//                                 .estado(true)
//                                 .build();

//                 response = new DisponibilidadResponse(
//                                 1L,
//                                 DiaSemana.MIERCOLES,
//                                 LocalTime.of(8, 0),
//                                 LocalTime.of(12, 0),
//                                 "Citas del Dr. Aguirre",
//                                 1L);
//         }

//         @Test
//         @DisplayName("Listar disponibilidades activas correctamente")
//         void listarDisponibilidades_debeRetornarListaDeRespuestas() {

//                 // Arrange

//                 when(disponibilidadRepository.findAll()).thenReturn(List.of(disponibilidad));
//                 when(disponibilidadMapper.toResponse(disponibilidad)).thenReturn(response);

//                 // Act
//                 List<DisponibilidadResponse> resultado = disponibilidadService.listar();

//                 // Assert
//                 assertThat(resultado).hasSize(1);
//                 assertThat(resultado.get(0).getNotas()).isEqualTo("Citas del Dr. Aguirre");

//                 verify(disponibilidadRepository).findAll();
//                 verify(disponibilidadMapper).toResponse(disponibilidad);
//         }

//         @Test
//         @DisplayName("Obtener disponibilidad por ID existente")
//         void obtenerDisponibilidadPorId_existente_debeRetornarLaDisponibilidad() {

//                 // Arrange

//                 when(disponibilidadRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(disponibilidad));
//                 when(disponibilidadMapper.toResponse(disponibilidad)).thenReturn(response);

//                 // Act
//                 DisponibilidadResponse resultado = disponibilidadService.obtenerPorId(1L);

//                 // Assert
//                 assertThat(resultado).isNotNull();
//                 assertThat(resultado.getId()).isEqualTo(1L);
//                 assertThat(resultado.getNotas()).isEqualTo("Citas del Dr. Aguirre");

//                 verify(disponibilidadRepository).findByIdAndEstadoIsTrue(1L);
//                 verify(disponibilidadMapper).toResponse(disponibilidad);
//         }

//         @Test
//         @DisplayName("Obtener disponibilidad por ID inexistente")
//         void obtenerDisponibilidadPorId_inexistente_debeLanzarExcepcion() {

//                 // Arrange
//                 when(disponibilidadRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

//                 // Act
//                 ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
//                                 () -> disponibilidadService.obtenerPorId(99L));

//                 // Assert
//                 assertThat(exception.getMessage()).contains("Disponibilidad no encontrada");
//         }

//         @Test
//         @DisplayName("Listar disponibilidades por ID de médico")
//         void listarDisponibilidades_PorMedicoId_DebeRetornar_ListaDeRespuestas() {

//                 // Arrange
//                 Disponibilidad disponibilidad2 = Disponibilidad.builder()
//                                 .id(2L)
//                                 .diaSemana(DiaSemana.MARTES)
//                                 .horaInicio(LocalTime.of(8, 0))
//                                 .horaFin(LocalTime.of(12, 0))
//                                 .notas("Citas del Dr. Aguirre")
//                                 .medico(medico)
//                                 .estado(true)
//                                 .build();

//                 DisponibilidadResponse response2 = new DisponibilidadResponse(
//                                 2L,
//                                 DiaSemana.MARTES,
//                                 LocalTime.of(8, 0),
//                                 LocalTime.of(12, 0),
//                                 "Citas del Dr. Aguirre",
//                                 1L);

//                 when(disponibilidadRepository.findAllByMedicoId(1L))
//                                 .thenReturn(List.of(disponibilidad, disponibilidad2));
//                 when(disponibilidadMapper.toResponse(disponibilidad)).thenReturn(response);
//                 when(disponibilidadMapper.toResponse(disponibilidad2)).thenReturn(response2);
//                 when(medicoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(medico));

//                 // Act
//                 List<DisponibilidadResponse> resultado = disponibilidadService.listarPorMedicoId(1L);

//                 // Assert
//                 assertThat(resultado).hasSize(2);
//                 assertThat(resultado.get(0).getId()).isEqualTo(1L);
//                 assertThat(resultado.get(1).getId()).isEqualTo(2L);
//                 assertThat(resultado.get(0).getMedicoId()).isEqualTo(1L);
//                 assertThat(resultado.get(0).getNotas()).isEqualTo("Citas del Dr. Aguirre");

//                 verify(medicoRepository).findByIdAndEstadoIsTrue(1L);
//                 verify(disponibilidadRepository).findAllByMedicoId(1L);
//                 verify(disponibilidadMapper).toResponse(disponibilidad);
//                 verify(disponibilidadMapper).toResponse(disponibilidad2);
//         }

//         @Test
//         @DisplayName("Listar disponibilidades por ID de médico inexistente")
//         void listarDisponibilidades_PorMedicoId_Inexistente_debeLanzarExcepcion() {

//                 // Arrange
//                 when(medicoRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

//                 // Act
//                 ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
//                                 () -> disponibilidadService.listarPorMedicoId(99L));

//                 // Assert
//                 assertThat(exception.getMessage()).contains("Médico no encontrado con ID: 99");
//         }

//         @Test
//         @DisplayName("Actualizar disponibilidad existente con nuevo médico")
//         void actualizarDisponibilidad_existenteConNuevoMedico_debeRetornarDisponibilidadActualizada() {

//                 // Arrange
//                 Medico nuevoMedico = new Medico();
//                 nuevoMedico.setId(99L);

//                 DisponibilidadRequest disponibilidadRequest = new DisponibilidadRequest(
//                                 DiaSemana.JUEVES,
//                                 LocalTime.of(9, 0),
//                                 LocalTime.of(13, 0),
//                                 "Citas del Dr. Aguirre",
//                                 99L,
//                                 45);

//                 Disponibilidad disponibilidadActualizada = Disponibilidad.builder()
//                                 .id(1L)
//                                 .diaSemana(DiaSemana.JUEVES)
//                                 .horaInicio(LocalTime.of(9, 0))
//                                 .horaFin(LocalTime.of(13, 0))
//                                 .notas("Citas del Dr. Aguirre")
//                                 .medico(nuevoMedico) // importante
//                                 .estado(true)
//                                 .build();

//                 DisponibilidadResponse responseActualizado = new DisponibilidadResponse(
//                                 1L,
//                                 DiaSemana.JUEVES,
//                                 LocalTime.of(9, 0),
//                                 LocalTime.of(13, 0),
//                                 "Citas del Dr. Aguirre",
//                                 99L);

//                 when(disponibilidadRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(disponibilidad));
//                 when(medicoRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.of(nuevoMedico));
//                 when(disponibilidadRepository.save(disponibilidad)).thenReturn(disponibilidadActualizada);
//                 when(disponibilidadMapper.toResponse(disponibilidadActualizada)).thenReturn(responseActualizado);

//                 // Act
//                 DisponibilidadResponse resultado = disponibilidadService.actualizar(1L, disponibilidadRequest);

//                 // Assert
//                 assertThat(resultado).isNotNull();
//                 assertThat(resultado.getDiaSemana()).isEqualTo(DiaSemana.JUEVES);
//                 assertThat(resultado.getHoraInicio()).isEqualTo(LocalTime.of(9, 0));
//                 assertThat(resultado.getHoraFin()).isEqualTo(LocalTime.of(13, 0));
//                 assertThat(resultado.getNotas()).isEqualTo("Citas del Dr. Aguirre");
//                 assertThat(resultado.getMedicoId()).isEqualTo(99L);

//                 verify(disponibilidadRepository).findByIdAndEstadoIsTrue(1L);
//                 verify(medicoRepository).findByIdAndEstadoIsTrue(99L);
//                 verify(disponibilidadRepository).save(disponibilidad);
//                 verify(disponibilidadMapper).toResponse(disponibilidadActualizada);
//         }

//         @Test
//         @DisplayName("Registrar disponibilidad correctamente")
//         void registrarDisponibilidad_debeRetornarRespuestaCorrecta() {
//                 // Arrange
//                 DisponibilidadRequest request = new DisponibilidadRequest(
//                                 DiaSemana.LUNES,
//                                 LocalTime.of(8, 0),
//                                 LocalTime.of(12, 0),
//                                 "Mañanas del Dr. Pérez",
//                                 1L,
//                                 45);

//                 when(medicoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(medico));
//                 when(disponibilidadRepository.existsByMedicoIdAndDiaSemanaAndHoraInicioAndHoraFin(
//                                 1L, DiaSemana.LUNES, LocalTime.of(8, 0), LocalTime.of(12, 0)))
//                                 .thenReturn(false);

//                 Disponibilidad entity = Disponibilidad.builder()
//                                 .id(10L)
//                                 .diaSemana(DiaSemana.LUNES)
//                                 .horaInicio(LocalTime.of(8, 0))
//                                 .horaFin(LocalTime.of(12, 0))
//                                 .notas("Mañanas del Dr. Pérez")
//                                 .medico(medico)
//                                 .estado(true)
//                                 .build();

//                 when(disponibilidadMapper.toEntity(request)).thenReturn(entity);
//                 when(disponibilidadRepository.save(entity)).thenReturn(entity);

//                 DisponibilidadResponse expectedResponse = new DisponibilidadResponse(
//                                 10L, DiaSemana.LUNES, LocalTime.of(8, 0), LocalTime.of(12, 0), "Mañanas del Dr. Pérez",
//                                 1L);

//                 when(disponibilidadMapper.toResponse(entity)).thenReturn(expectedResponse);

//                 // Act
//                 DisponibilidadResponse response = disponibilidadService.registrar(request);

//                 // Assert
//                 assertThat(response).isNotNull();
//                 assertThat(response.getId()).isEqualTo(10L);
//                 assertThat(response.getDiaSemana()).isEqualTo(DiaSemana.LUNES);
//                 assertThat(response.getNotas()).isEqualTo("Mañanas del Dr. Pérez");

//                 verify(medicoRepository).findByIdAndEstadoIsTrue(1L);
//                 verify(disponibilidadRepository).existsByMedicoIdAndDiaSemanaAndHoraInicioAndHoraFin(
//                                 1L, DiaSemana.LUNES, LocalTime.of(8, 0), LocalTime.of(12, 0));
//                 verify(disponibilidadRepository).save(entity);
//                 verify(disponibilidadMapper).toEntity(request);
//                 verify(disponibilidadMapper).toResponse(entity);
//         }

//         @Test
//         @DisplayName("Registrar disponibilidad con médico inexistente debe lanzar excepción")
//         void registrarDisponibilidad_medicoInexistente_debeLanzarExcepcion() {
//                 // Arrange
//                 DisponibilidadRequest request = new DisponibilidadRequest(
//                                 DiaSemana.LUNES,
//                                 LocalTime.of(8, 0),
//                                 LocalTime.of(12, 0),
//                                 "Turno mañana",
//                                 999L,
//                                 45);

//                 when(medicoRepository.findByIdAndEstadoIsTrue(999L)).thenReturn(Optional.empty());

//                 // Act + Assert
//                 ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
//                                 () -> disponibilidadService.registrar(request));

//                 assertThat(exception.getMessage()).contains("Médico no encontrado con ID: 999");
//                 verify(medicoRepository).findByIdAndEstadoIsTrue(999L);
//         }

//         @Test
//         @DisplayName("Registrar disponibilidad duplicada debe lanzar excepción")
//         void registrarDisponibilidad_duplicada_debeLanzarExcepcion() {
//                 // Arrange
//                 DisponibilidadRequest request = new DisponibilidadRequest(
//                                 DiaSemana.LUNES,
//                                 LocalTime.of(8, 0),
//                                 LocalTime.of(12, 0),
//                                 "Turno duplicado",
//                                 1L,
//                                 45);

//                 when(medicoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(medico));
//                 when(disponibilidadRepository.existsByMedicoIdAndDiaSemanaAndHoraInicioAndHoraFin(
//                                 1L, DiaSemana.LUNES, LocalTime.of(8, 0), LocalTime.of(12, 0)))
//                                 .thenReturn(true);

//                 // Act + Assert
//                 IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                                 () -> disponibilidadService.registrar(request));

//                 assertThat(exception.getMessage()).contains("Ya existe una disponibilidad para ese médico");

//                 verify(medicoRepository).findByIdAndEstadoIsTrue(1L);
//                 verify(disponibilidadRepository).existsByMedicoIdAndDiaSemanaAndHoraInicioAndHoraFin(
//                                 1L, DiaSemana.LUNES, LocalTime.of(8, 0), LocalTime.of(12, 0));
//         }

// }