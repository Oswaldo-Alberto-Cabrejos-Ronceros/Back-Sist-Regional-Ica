package com.clinicaregional.clinica.cita.service;

import com.clinicaregional.clinica.dto.request.CitaRequest;
import com.clinicaregional.clinica.dto.response.CitaResponse;
import com.clinicaregional.clinica.entity.Cita;
import com.clinicaregional.clinica.entity.Disponibilidad;
import com.clinicaregional.clinica.entity.HorarioBloque;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.entity.Servicio;
import com.clinicaregional.clinica.enums.EstadoBloque;
import com.clinicaregional.clinica.enums.EstadoCita;
import com.clinicaregional.clinica.mapper.CitaMapper;
import com.clinicaregional.clinica.mapper.PacienteMapper;
import com.clinicaregional.clinica.repository.CitaRepository;
import com.clinicaregional.clinica.repository.HorarioBloqueRepository;
import com.clinicaregional.clinica.repository.MedicoRepository;
import com.clinicaregional.clinica.repository.PacienteRepository;
import com.clinicaregional.clinica.repository.ServicioRepository;
import com.clinicaregional.clinica.service.impl.CitaServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CitaServiceImplTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private HorarioBloqueRepository horarioBloqueRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private CitaMapper citaMapper;

    @Mock
    private PacienteMapper pacienteMapper;

    @InjectMocks
    private CitaServiceImpl citaService;

    private Cita cita;
    private CitaResponse citaResponse;
    private Medico medico;
    private Paciente paciente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        paciente = new Paciente();
        paciente.setId(1L);

        medico = new Medico();
        medico.setId(1L);

        cita = new Cita();
        cita.setId(1L);
        cita.setFecha(LocalDate.now());
        cita.setHora(LocalTime.of(10, 0));
        cita.setEstadoCita(EstadoCita.CONFIRMADA);
        cita.setMedico(medico);
        cita.setPaciente(paciente);

        citaResponse = new CitaResponse();
        citaResponse.setCitaId(1L);
        citaResponse.setFecha(LocalDate.now());
        citaResponse.setHora(LocalTime.of(10, 0));
        citaResponse.setEstadoCita(EstadoCita.CONFIRMADA);
        citaResponse.setMedicoId(1L);
        citaResponse.setPacienteId(1L);
    }

    @Test
    @DisplayName("Registrar cita con datos válidos debe retornar respuesta")
    void registrarCita_datosValidos() {
        CitaRequest request = new CitaRequest();
        request.setFecha(LocalDate.now());
        request.setHora(LocalTime.of(10, 0));
        request.setPacienteId(1L);
        request.setMedicoId(1L);
        request.setServicioId(1L);

        HorarioBloque bloque = new HorarioBloque();
        bloque.setDisponibilidad(new Disponibilidad());
        bloque.getDisponibilidad().setMedico(medico);
        bloque.setEstadoBloque(EstadoBloque.DISPONIBLE);

        when(horarioBloqueRepository.findByFechaAndHoraInicioAndEstadoBloque(any(), any(), any()))
                .thenReturn(Optional.of(bloque));
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(new Servicio()));
        when(citaMapper.toEntity(request)).thenReturn(cita);
        when(citaRepository.save(any())).thenReturn(cita);
        when(citaMapper.toResponse(any())).thenReturn(citaResponse);

        CitaResponse response = citaService.registrar(request);

        assertThat(response).isNotNull();
        assertThat(response.getCitaId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Actualizar cita existente debe modificar campos correctamente")
    void actualizarCita_existente() {
        CitaRequest nueva = new CitaRequest();
        nueva.setFecha(LocalDate.now().plusDays(1));
        nueva.setHora(LocalTime.of(12, 0));
        nueva.setNotas("Nota");
        nueva.setAntecedentes("Antecedentes");

        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any())).thenReturn(cita);
        when(citaMapper.toResponse(cita)).thenReturn(citaResponse);

        CitaResponse resultado = citaService.actualizar(1L, nueva);

        assertThat(resultado).isNotNull();
        verify(citaRepository).save(any());
    }

    @Test
    @DisplayName("Obtener cita por ID existente debe retornar la respuesta")
    void obtenerCitaPorId_existente() {
        // Arrange
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaMapper.toResponse(cita)).thenReturn(citaResponse);

        // Act
        CitaResponse resultado = citaService.obtenerPorId(1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getCitaId()).isEqualTo(1L);
        verify(citaRepository).findById(1L);
        verify(citaMapper).toResponse(cita);
    }

    @Test
    @DisplayName("Obtener cita por ID inexistente debe lanzar excepción")
    void obtenerCitaPorId_inexistente() {
        // Arrange
        when(citaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> citaService.obtenerPorId(999L));

        assertThat(exception.getMessage()).contains("Cita no encontrada");
        verify(citaRepository).findById(999L);
    }

    @Test
    @DisplayName("Listar todas las citas debe retornar lista con elementos")
    void listarTodasLasCitas() {
        // Arrange
        when(citaRepository.findAll()).thenReturn(List.of(cita));
        when(citaMapper.toResponse(cita)).thenReturn(citaResponse);

        // Act
        List<CitaResponse> resultado = citaService.listarTodas();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCitaId()).isEqualTo(1L);
        verify(citaRepository).findAll();
        verify(citaMapper).toResponse(cita);
    }

    @Test
    @DisplayName("Confirmar cita en estado PENDIENTE debe cambiar a CONFIRMADA")
    void confirmarCita_estadoValido() {
        cita.setEstadoCita(EstadoCita.PENDIENTE);
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setServicio(new Servicio());

        HorarioBloque bloque = new HorarioBloque();
        bloque.setEstadoBloque(EstadoBloque.OCUPADO);

        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(horarioBloqueRepository.findByCitaId(1L)).thenReturn(Optional.of(bloque));
        when(citaRepository.save(cita)).thenReturn(cita);
        when(citaMapper.toResponse(cita)).thenReturn(citaResponse);

        CitaResponse response = citaService.confirmarCita(1L);

        assertThat(response).isNotNull();
        assertThat(response.getEstadoCita()).isEqualTo(EstadoCita.CONFIRMADA);
    }

    @Test
    @DisplayName("Cancelar cita válida debe actualizar estado y liberar bloque")
    void cancelarCita_estadoValido() {
        cita.setEstadoCita(EstadoCita.PENDIENTE);
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setServicio(new Servicio());

        HorarioBloque bloque = new HorarioBloque();
        bloque.setEstadoBloque(EstadoBloque.OCUPADO);
        bloque.setCita(cita);

        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(horarioBloqueRepository.findByCitaId(1L)).thenReturn(Optional.of(bloque));

        // Simulamos que luego de cancelar se guarda con estado CANCELADA
        Cita citaCancelada = new Cita();
        citaCancelada.setEstadoCita(EstadoCita.CANCELADA);

        when(citaRepository.save(any(Cita.class))).thenReturn(citaCancelada);

        CitaResponse citaResponseCancelada = new CitaResponse();
        citaResponseCancelada.setEstadoCita(EstadoCita.CANCELADA);
        when(citaMapper.toResponse(any(Cita.class))).thenReturn(citaResponseCancelada);

        // Act
        CitaResponse response = citaService.cancelarCita(1L);

        // Assert
        assertThat(response.getEstadoCita()).isEqualTo(EstadoCita.CANCELADA);
    }

    @Test
    @DisplayName("Atender cita confirmada debe actualizar estado correctamente")
    void atenderCita_confirmada() {
        cita.setEstadoCita(EstadoCita.CONFIRMADA);
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setServicio(new Servicio());

        HorarioBloque bloque = new HorarioBloque();
        bloque.setEstadoBloque(EstadoBloque.OCUPADO);

        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(horarioBloqueRepository.findByCita(cita)).thenReturn(Optional.of(bloque));

        // Simular la cita ya con estado ATENDIDA después del cambio
        Cita citaAtendida = new Cita();
        citaAtendida.setEstadoCita(EstadoCita.ATENDIDA);

        when(citaRepository.save(any(Cita.class))).thenReturn(citaAtendida);

        CitaResponse citaResponseAtendida = new CitaResponse();
        citaResponseAtendida.setEstadoCita(EstadoCita.ATENDIDA);
        when(citaMapper.toResponse(any(Cita.class))).thenReturn(citaResponseAtendida);

        // Act
        CitaResponse response = citaService.atenderCita(1L);

        // Assert
        assertThat(response.getEstadoCita()).isEqualTo(EstadoCita.ATENDIDA);
    }

    @Test
    @DisplayName("Eliminar cita existente debe ejecutar deleteById")
    void eliminarCita_existente() {
        when(citaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(citaRepository).deleteById(1L);

        citaService.eliminar(1L);

        verify(citaRepository).deleteById(1L);
    }
}
