// package com.clinicaregional.clinica.medicoEspecialidad.service;

// import com.clinicaregional.clinica.dto.request.MedicoEspecialidadRequest;
// import com.clinicaregional.clinica.dto.response.MedicoEspecialidadResponse;
// import com.clinicaregional.clinica.entity.Especialidad;
// import com.clinicaregional.clinica.entity.Medico;
// import com.clinicaregional.clinica.entity.MedicoEspecialidad;
// import com.clinicaregional.clinica.entity.MedicoEspecialidadId;
// import com.clinicaregional.clinica.mapper.MedicoEspecialidadMapper;
// import com.clinicaregional.clinica.repository.MedicoEspecialidadRepository;
// import com.clinicaregional.clinica.repository.MedicoRepository;
// import com.clinicaregional.clinica.repository.EspecialidadRepository;
// import com.clinicaregional.clinica.service.impl.MedicoEspecialidadServiceImpl;
// import com.clinicaregional.clinica.util.FiltroEstado;
// import jakarta.persistence.EntityExistsException;
// import jakarta.persistence.EntityNotFoundException;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.Optional;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.*;

// class MedicoEspecialidadServiceImplTest {

//     @Mock
//     private MedicoEspecialidadRepository medicoEspecialidadRepository;

//     @Mock
//     private MedicoRepository medicoRepository;

//     @Mock
//     private EspecialidadRepository especialidadRepository;

//     @Mock
//     private FiltroEstado filtroEstado;

//     @InjectMocks
//     private MedicoEspecialidadServiceImpl medicoEspecialidadService;

//     private MedicoEspecialidadRequest request;
//     private MedicoEspecialidad entity;
//     private MedicoEspecialidadResponse response;

//     private Medico medico;
//     private Especialidad especialidad;

//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);

//         medico = new Medico();
//         medico.setId(1L);

//         especialidad = new Especialidad();
//         especialidad.setId(2L);

//         request = new MedicoEspecialidadRequest(1L, 2L, LocalDate.now());
//         entity = MedicoEspecialidadMapper.toEntity(request, medico, especialidad);
//         response = MedicoEspecialidadMapper.toResponse(entity);
//     }

//     @Test
//     @DisplayName("Registrar nueva relación Médico-Especialidad")
//     void registrarRelacionME() {
//         // Arrange
//         when(medicoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(medico));
//         when(especialidadRepository.findByIdAndEstadoIsTrue(2L)).thenReturn(Optional.of(especialidad));
//         when(medicoEspecialidadRepository.existsByMedicoAndEspecialidad(medico, especialidad)).thenReturn(false);
//         when(medicoEspecialidadRepository.save(any())).thenReturn(entity);

//         // Act
//         MedicoEspecialidadResponse resultado = medicoEspecialidadService.registrarRelacionME(request);

//         // Assert
//         assertThat(resultado).isNotNull();
//         verify(medicoEspecialidadRepository, times(1)).save(any());
//     }

//     @Test
//     @DisplayName("Registrar relación existente debe lanzar EntityExistsException")
//     void registrarRelacionME_existente() {
//         // Arrange
//         when(medicoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(medico));
//         when(especialidadRepository.findByIdAndEstadoIsTrue(2L)).thenReturn(Optional.of(especialidad));
//         when(medicoEspecialidadRepository.existsByMedicoAndEspecialidad(medico, especialidad)).thenReturn(true);

//         // Act & Assert
//         assertThrows(EntityExistsException.class, () -> medicoEspecialidadService.registrarRelacionME(request));
//         verify(medicoEspecialidadRepository, never()).save(any());
//     }

//     @Test
//     @DisplayName("Actualizar relación Médico-Especialidad existente")
//     void actualizarRelacionME() {
//         // Arrange
//         MedicoEspecialidadId id = new MedicoEspecialidadId(1L, 2L);
//         when(medicoEspecialidadRepository.findByIdAndEstadoIsTrue(id)).thenReturn(Optional.of(entity));
//         when(medicoEspecialidadRepository.save(any())).thenReturn(entity);

//         // Act
//         MedicoEspecialidadResponse resultado = medicoEspecialidadService.actualizarRelacionME(1L, 2L, request);

//         // Assert
//         assertThat(resultado).isNotNull();
//         verify(medicoEspecialidadRepository, times(1)).save(any());
//     }

//     @Test
//     @DisplayName("Actualizar relación inexistente debe lanzar RuntimeException")
//     void actualizarRelacionME_inexistente() {
//         // Arrange
//         MedicoEspecialidadId id = new MedicoEspecialidadId(1L, 2L);
//         when(medicoEspecialidadRepository.findByIdAndEstadoIsTrue(id)).thenReturn(Optional.empty());

//         // Act & Assert
//         assertThrows(RuntimeException.class, () -> medicoEspecialidadService.actualizarRelacionME(1L, 2L, request));
//     }

//     @Test
//     @DisplayName("Eliminar relación Médico-Especialidad existente")
//     void eliminarRelacionME() {
//         // Arrange
//         MedicoEspecialidadId id = new MedicoEspecialidadId(1L, 2L);
//         when(medicoEspecialidadRepository.findByIdAndEstadoIsTrue(id)).thenReturn(Optional.of(entity));

//         // Act
//         medicoEspecialidadService.eliminarRelacionME(1L, 2L);

//         // Assert
//         assertThat(entity.getEstado()).isFalse();
//         verify(medicoEspecialidadRepository, times(1)).save(entity);
//     }

//     @Test
//     @DisplayName("Eliminar relación inexistente debe lanzar RuntimeException")
//     void eliminarRelacionME_inexistente() {
//         // Arrange
//         MedicoEspecialidadId id = new MedicoEspecialidadId(1L, 2L);
//         when(medicoEspecialidadRepository.findByIdAndEstadoIsTrue(id)).thenReturn(Optional.empty());

//         // Act & Assert
//         assertThrows(RuntimeException.class, () -> medicoEspecialidadService.eliminarRelacionME(1L, 2L));
//     }

//     @Test
//     @DisplayName("Obtener especialidades de un médico")
//     void obtenerEspecialidadDelMedico() {
//         // Arrange
//         when(medicoEspecialidadRepository.findByMedicoId(1L)).thenReturn(List.of(entity));

//         // Act
//         List<MedicoEspecialidadResponse> resultado = medicoEspecialidadService.obtenerEspecialidadDelMedico(1L);

//         // Assert
//         assertThat(resultado).isNotEmpty();
//         verify(medicoEspecialidadRepository, times(1)).findByMedicoId(1L);
//     }

//     @Test
//     @DisplayName("Obtener médicos de una especialidad")
//     void obtenerMedicosPorEspecialidad() {
//         // Arrange
//         when(medicoEspecialidadRepository.findByEspecialidadId(2L)).thenReturn(List.of(entity));

//         // Act
//         List<MedicoEspecialidadResponse> resultado = medicoEspecialidadService.obtenerMedicosPorEspecialidad(2L);

//         // Assert
//         assertThat(resultado).isNotEmpty();
//         verify(medicoEspecialidadRepository, times(1)).findByEspecialidadId(2L);
//     }
// }
