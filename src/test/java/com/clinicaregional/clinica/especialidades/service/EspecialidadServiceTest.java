// package com.clinicaregional.clinica.especialidades.service;

// import com.clinicaregional.clinica.dto.request.EspecialidadRequest;
// import com.clinicaregional.clinica.dto.response.EspecialidadResponse;
// import com.clinicaregional.clinica.entity.Especialidad;
// import com.clinicaregional.clinica.exception.ResourceNotFoundException;
// import com.clinicaregional.clinica.mapper.EspecialidadMapper;
// import com.clinicaregional.clinica.repository.EspecialidadRepository;
// import com.clinicaregional.clinica.service.impl.EspecialidadServiceImpl;
// import com.clinicaregional.clinica.util.FiltroEstado;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.util.List;
// import java.util.Optional;

// import static org.assertj.core.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class EspecialidadServiceTest {

//     @Mock
//     private EspecialidadRepository especialidadRepository;

//     @Mock
//     private FiltroEstado filtroEstado;

//     @Mock
//     private EspecialidadMapper especialidadMapper;

//     @InjectMocks
//     private EspecialidadServiceImpl especialidadService;

//     private Especialidad especialidad;
//     private EspecialidadRequest especialidadRequest;
//     private EspecialidadResponse especialidadResponse;

//     @BeforeEach
//     void setUp() {
//         especialidad = Especialidad.builder()
//                 .id(1L)
//                 .nombre("Cardiología")
//                 .descripcion("Corazón y sistema circulatorio")
//                 .imagen("cardiologia.png")
//                 .estado(true)
//                 .build();

//         especialidadRequest = new EspecialidadRequest(
//                 "Cardiología", "Corazón y sistema circulatorio", "cardiologia.png");

//         especialidadResponse = new EspecialidadResponse(
//                 1L, "Cardiología", "Corazón y sistema circulatorio", "cardiologia.png");

//         doNothing().when(filtroEstado).activarFiltroEstado(true);
//     }

//     @Test
//     void listarEspecialidades_exitoso() {
//         when(especialidadRepository.findAll()).thenReturn(List.of(especialidad));
//         when(especialidadMapper.toResponse(any())).thenReturn(especialidadResponse);

//         List<EspecialidadResponse> especialidades = especialidadService.listarEspecialidades();

//         assertThat(especialidades).hasSize(1);
//         assertThat(especialidades.get(0).getNombre()).isEqualTo("Cardiología");
//     }

//     @Test
//     void getEspecialidadById_existente() {
//         when(especialidadRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(especialidad));
//         when(especialidadMapper.toResponse(any())).thenReturn(especialidadResponse);

//         Optional<EspecialidadResponse> result = especialidadService.getEspecialidadById(1L);

//         assertThat(result).isPresent();
//         assertThat(result.get().getNombre()).isEqualTo("Cardiología");
//     }

//     @Test
//     void getEspecialidadById_noExistente() {
//         when(especialidadRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

//         assertThatThrownBy(() -> especialidadService.getEspecialidadById(99L))
//                 .isInstanceOf(ResourceNotFoundException.class)
//                 .hasMessageContaining("No existe una especialidad con el id");
//     }

//     @Test
//     void guardarEspecialidad_exitoso() {
//         // Arrange
//         when(especialidadRepository.existsByNombre(any())).thenReturn(false);
//         when(especialidadMapper.toEntity(any(EspecialidadRequest.class))).thenReturn(especialidad);
//         when(especialidadRepository.save(especialidad)).thenReturn(especialidad);
//         when(especialidadMapper.toResponse(especialidad)).thenReturn(especialidadResponse);

//         // Act
//         EspecialidadResponse response = especialidadService.guardarEspecialidad(especialidadRequest);

//         // Assert
//         assertThat(response).isNotNull();
//         assertThat(response.getNombre()).isEqualTo("Cardiología");
//     }

//     @Test
//     void guardarEspecialidad_nombreExistente_deberiaLanzarExcepcion() {
//         when(especialidadRepository.existsByNombre(any())).thenReturn(true);

//         assertThatThrownBy(() -> especialidadService.guardarEspecialidad(especialidadRequest))
//                 .isInstanceOf(RuntimeException.class)
//                 .hasMessageContaining("Ya existe una especialidad con el nombre ingresado");
//     }

//     @Test
//     void actualizarEspecialidad_exitoso() {
//         when(especialidadRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(especialidad));
//         when(especialidadRepository.save(any(Especialidad.class))).thenReturn(especialidad);
//         when(especialidadMapper.toResponse(any())).thenReturn(especialidadResponse);

//         EspecialidadResponse response = especialidadService.actualizarEspecialidad(1L, especialidadRequest);

//         assertThat(response.getNombre()).isEqualTo("Cardiología");
//     }

//     @Test
//     void actualizarEspecialidad_noExistente_deberiaLanzarExcepcion() {
//         when(especialidadRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

//         assertThatThrownBy(() -> especialidadService.actualizarEspecialidad(99L, especialidadRequest))
//                 .isInstanceOf(ResourceNotFoundException.class)
//                 .hasMessageContaining("Especialidad no encontrada");
//     }

//     @Test
//     void eliminarEspecialidad_exitoso() {
//         Especialidad especialidadExistente = Especialidad.builder()
//                 .id(1L)
//                 .nombre("Cardiología")
//                 .descripcion("Corazón")
//                 .imagen("cardiologia.png")
//                 .estado(true)
//                 .build();

//         when(especialidadRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(especialidadExistente));

//         especialidadService.eliminarEspecialidad(1L);

//         assertThat(especialidadExistente.getEstado()).isFalse();
//         verify(especialidadRepository, times(1)).save(especialidadExistente);
//     }
// }
