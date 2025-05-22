// package com.clinicaregional.clinica.medicoEspecialidad.service;

// import com.clinicaregional.clinica.dto.request.MedicoEspecialidadRequest;
// import com.clinicaregional.clinica.dto.response.MedicoEspecialidadResponse;
// import com.clinicaregional.clinica.service.MedicoEspecialidadService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;

// import java.time.LocalDate;
// import java.util.List;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.*;

// class MedicoEspecialidadServiceTest {

//     @Mock
//     private MedicoEspecialidadService medicoEspecialidadService;

//     private MedicoEspecialidadRequest medicoEspecialidadRequest;
//     private MedicoEspecialidadResponse medicoEspecialidadResponse;

//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);
//         medicoEspecialidadRequest = new MedicoEspecialidadRequest(1L, 2L, LocalDate.now());
//         medicoEspecialidadResponse = new MedicoEspecialidadResponse(1L, "Juan Pérez", "12345678901", "987654321", 2L,
//                 "Cardiología", LocalDate.now());
//     }

//     @Test
//     @DisplayName("Registrar nueva relación Médico-Especialidad")
//     void registrarRelacionME() {
//         when(medicoEspecialidadService.registrarRelacionME(any())).thenReturn(medicoEspecialidadResponse);

//         MedicoEspecialidadResponse resultado = medicoEspecialidadService.registrarRelacionME(medicoEspecialidadRequest);

//         assertThat(resultado).isNotNull();
//         assertThat(resultado.getNumeroColegiatura()).isEqualTo("12345678901");
//         assertThat(resultado.getNumeroRNE()).isEqualTo("987654321");
//         verify(medicoEspecialidadService, times(1)).registrarRelacionME(any());
//     }

//     @Test
//     @DisplayName("Actualizar relación Médico-Especialidad existente")
//     void actualizarRelacionME() {
//         // Arrange
//         when(medicoEspecialidadService.actualizarRelacionME(eq(1L), eq(2L), any()))
//                 .thenReturn(medicoEspecialidadResponse);

//         // Act
//         MedicoEspecialidadResponse resultado = medicoEspecialidadService.actualizarRelacionME(1L, 2L,
//                 medicoEspecialidadRequest);

//         // Assert
//         assertThat(resultado).isNotNull();
//         verify(medicoEspecialidadService, times(1)).actualizarRelacionME(eq(1L), eq(2L), any());
//     }

//     @Test
//     @DisplayName("Eliminar relación Médico-Especialidad")
//     void eliminarRelacionME() {
//         // Arrange
//         doNothing().when(medicoEspecialidadService).eliminarRelacionME(1L, 2L);

//         // Act
//         medicoEspecialidadService.eliminarRelacionME(1L, 2L);

//         // Assert
//         verify(medicoEspecialidadService, times(1)).eliminarRelacionME(1L, 2L);
//     }

//     @Test
//     @DisplayName("Obtener especialidades de un médico")
//     void obtenerEspecialidadDelMedico() {
//         // Arrange
//         when(medicoEspecialidadService.obtenerEspecialidadDelMedico(1L))
//                 .thenReturn(List.of(medicoEspecialidadResponse));

//         // Act
//         List<MedicoEspecialidadResponse> resultado = medicoEspecialidadService.obtenerEspecialidadDelMedico(1L);

//         // Assert
//         assertThat(resultado).isNotEmpty();
//         verify(medicoEspecialidadService, times(1)).obtenerEspecialidadDelMedico(1L);
//     }

//     @Test
//     @DisplayName("Obtener médicos de una especialidad")
//     void obtenerMedicosPorEspecialidad() {
//         // Arrange
//         when(medicoEspecialidadService.obtenerMedicosPorEspecialidad(2L))
//                 .thenReturn(List.of(medicoEspecialidadResponse));

//         // Act
//         List<MedicoEspecialidadResponse> resultado = medicoEspecialidadService.obtenerMedicosPorEspecialidad(2L);

//         // Assert
//         assertThat(resultado).isNotEmpty();
//         verify(medicoEspecialidadService, times(1)).obtenerMedicosPorEspecialidad(2L);
//     }
// }
