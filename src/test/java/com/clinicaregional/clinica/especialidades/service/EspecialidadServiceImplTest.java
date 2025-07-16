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
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;

// import java.util.List;
// import java.util.Optional;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.Mockito.*;

// class EspecialidadServiceImplTest {

//     @Mock
//     private EspecialidadRepository especialidadRepository;

//     @Mock
//     private EspecialidadMapper especialidadMapper;

//     @Mock
//     private FiltroEstado filtroEstado;

//     @InjectMocks
//     private EspecialidadServiceImpl especialidadService;

//     private Especialidad especialidad;
//     private EspecialidadRequest request;
//     private EspecialidadResponse response;

//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);

//         especialidad = Especialidad.builder()
//                 .id(1L)
//                 .nombre("Cardiología")
//                 .descripcion("Especialidad del corazón")
//                 .imagen("cardio.jpg")
//                 .estado(true)
//                 .build();

//         request = new EspecialidadRequest("Cardiología", "Especialidad del corazón", "cardio.jpg");

//         response = new EspecialidadResponse(
//                 1L, "Cardiología", "Especialidad del corazón", "cardio.jpg"
//         );

//         doNothing().when(filtroEstado).activarFiltroEstado(true);
//     }

//     @Test
//     @DisplayName("Listar todas las especialidades activas")
//     void listarEspecialidades_debeRetornarLista() {
//         when(especialidadRepository.findAll()).thenReturn(List.of(especialidad));
//         when(especialidadMapper.toResponse(especialidad)).thenReturn(response);

//         List<EspecialidadResponse> resultado = especialidadService.listarEspecialidades();

//         assertThat(resultado).hasSize(1);
//         assertThat(resultado.get(0).getNombre()).isEqualTo("Cardiología");
//     }

//     @Test
//     @DisplayName("Obtener especialidad por ID existente")
//     void getEspecialidadById_existente() {
//         when(especialidadRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(especialidad));
//         when(especialidadMapper.toResponse(especialidad)).thenReturn(response);

//         Optional<EspecialidadResponse> resultado = especialidadService.getEspecialidadById(1L);

//         assertThat(resultado).isPresent();
//         assertThat(resultado.get().getNombre()).isEqualTo("Cardiología");
//     }

//     @Test
//     @DisplayName("Guardar nueva especialidad exitosamente")
//     void guardarEspecialidad_exitoso() {
//         Especialidad nueva = Especialidad.builder()
//                 .nombre("Neurología")
//                 .descripcion("Especialidad del cerebro")
//                 .imagen("neuro.jpg")
//                 .estado(true)
//                 .build();

//         EspecialidadRequest req = new EspecialidadRequest("Neurología", "Especialidad del cerebro", "neuro.jpg");
//         EspecialidadResponse res = new EspecialidadResponse(2L, "Neurología", "Especialidad del cerebro", "neuro.jpg");

//         when(especialidadRepository.existsByNombre("Neurología")).thenReturn(false);
//         when(especialidadMapper.toEntity(req)).thenReturn(nueva);
//         when(especialidadRepository.save(nueva)).thenReturn(nueva);
//         when(especialidadMapper.toResponse(nueva)).thenReturn(res);

//         EspecialidadResponse resultado = especialidadService.guardarEspecialidad(req);

//         assertThat(resultado.getNombre()).isEqualTo("Neurología");
//     }

//     @Test
//     @DisplayName("Guardar especialidad con nombre repetido debe lanzar excepción")
//     void guardarEspecialidad_nombreDuplicado_debeLanzarExcepcion() {
//         when(especialidadRepository.existsByNombre("Cardiología")).thenReturn(true);

//         assertThrows(RuntimeException.class, () -> especialidadService.guardarEspecialidad(request));
//     }

//     @Test
//     @DisplayName("Actualizar especialidad existente exitosamente")
//     void actualizarEspecialidad_existente() {
//         EspecialidadRequest nuevoRequest = new EspecialidadRequest("Neurología", "Especialidad del cerebro", "neuro.jpg");
//         EspecialidadResponse nuevoResponse = new EspecialidadResponse(1L, "Neurología", "Especialidad del cerebro", "neuro.jpg");

//         when(especialidadRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(especialidad));
//         when(especialidadRepository.existsByNombre("Neurología")).thenReturn(false);
//         when(especialidadRepository.save(especialidad)).thenReturn(especialidad);
//         when(especialidadMapper.toResponse(especialidad)).thenReturn(nuevoResponse);

//         EspecialidadResponse resultado = especialidadService.actualizarEspecialidad(1L, nuevoRequest);

//         assertThat(resultado.getNombre()).isEqualTo("Neurología");
//     }

//     @Test
//     @DisplayName("Actualizar especialidad inexistente debe lanzar excepción")
//     void actualizarEspecialidad_inexistente() {
//         when(especialidadRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());
//         EspecialidadRequest nuevoRequest = new EspecialidadRequest("Neurología", "Especialidad del cerebro", "neuro.jpg");

//         assertThrows(ResourceNotFoundException.class, () -> especialidadService.actualizarEspecialidad(99L, nuevoRequest));
//     }

//     @Test
//     @DisplayName("Eliminar especialidad existente exitosamente")
//     void eliminarEspecialidad_existente() {
//         when(especialidadRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(especialidad));

//         especialidadService.eliminarEspecialidad(1L);

//         assertThat(especialidad.getEstado()).isFalse();
//         verify(especialidadRepository).save(especialidad);
//     }

//     @Test
//     @DisplayName("Eliminar especialidad inexistente debe lanzar excepción")
//     void eliminarEspecialidad_inexistente() {
//         when(especialidadRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

//         assertThrows(ResourceNotFoundException.class, () -> especialidadService.eliminarEspecialidad(99L));
//     }
// }
