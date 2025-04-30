package com.clinicaregional.clinica.pacienteAlergia.service;

import com.clinicaregional.clinica.dto.AlergiaDTO;
import com.clinicaregional.clinica.dto.PacienteAlergiaDTO;
import com.clinicaregional.clinica.dto.PacienteDTO;
import com.clinicaregional.clinica.entity.Alergia;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.entity.PacienteAlergia;
import com.clinicaregional.clinica.enums.Gravedad;
import com.clinicaregional.clinica.enums.TipoAlergia;
import com.clinicaregional.clinica.mapper.AlergiaMapper;
import com.clinicaregional.clinica.mapper.PacienteAlergiaMapper;
import com.clinicaregional.clinica.mapper.PacienteMapper;
import com.clinicaregional.clinica.repository.PacienteAlergiaRepository;
import com.clinicaregional.clinica.service.AlergiaService;
import com.clinicaregional.clinica.service.PacienteService;
import com.clinicaregional.clinica.service.impl.PacienteAlergiaServiceImpl;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class PacienteAlergiaServiceImplTest {

    @Mock
    private PacienteAlergiaRepository pacienteAlergiaRepository;

    @Mock
    private PacienteAlergiaMapper pacienteAlergiaMapper;

    @Mock
    private PacienteService pacienteService;

    @Mock
    private AlergiaMapper alergiaMapper;
    @Mock
    private PacienteMapper pacienteMapper;

    @Mock
    private AlergiaService alergiaService;

    @Mock
    private FiltroEstado filtroEstado;

    @InjectMocks
    private PacienteAlergiaServiceImpl pacienteAlergiaService;

    private PacienteAlergia pacienteAlergia;
    private PacienteAlergiaDTO pacienteAlergiaDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Paciente paciente = new Paciente();
        paciente.setId(1L);
        paciente.setNombres("Juan Perez");

        Alergia alergia = new Alergia();
        alergia.setId(1L);
        alergia.setNombre("Polen");
        alergia.setTipoAlergia(TipoAlergia.AMBIENTAL);

        pacienteAlergia = new PacienteAlergia();
        pacienteAlergia.setId(1L);
        pacienteAlergia.setPaciente(paciente);
        pacienteAlergia.setAlergia(alergia);
        pacienteAlergia.setEstado(true);

        pacienteAlergiaDTO = new PacienteAlergiaDTO();
        pacienteAlergiaDTO.setId(1L);
        pacienteAlergiaDTO.setPacienteId(1L);
        pacienteAlergiaDTO.setAlergia(new AlergiaDTO(1L, "Polen", TipoAlergia.AMBIENTAL));
        pacienteAlergiaDTO.setGravedad(Gravedad.MODERADA);
    }

    @Test
    @DisplayName("Listar todas las relaciones Paciente-Alergia")
    void listarPacienteAlergias() {
        // Arrange
        when(pacienteAlergiaRepository.findAll()).thenReturn(List.of(pacienteAlergia));
        when(pacienteAlergiaMapper.mapToPacienteAlergiaDTO(any())).thenReturn(pacienteAlergiaDTO);

        // Act
        List<PacienteAlergiaDTO> resultado = pacienteAlergiaService.listarPacienteAlergias();

        // Assert
        assertThat(resultado).hasSize(1);
        verify(pacienteAlergiaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Listar relaciones Paciente-Alergia por paciente ID")
    void listarPacienteAlergiasPorPaciente() {
        // Arrange
        PacienteDTO pacienteDTO = new PacienteDTO();
        pacienteDTO.setId(1L);
        pacienteDTO.setNombres("Juan Perez");

        when(pacienteService.getPacientePorId(1L)).thenReturn(Optional.of(pacienteDTO));
        when(pacienteMapper.mapToPaciente(any(PacienteDTO.class))).thenReturn(new Paciente());
        when(pacienteAlergiaRepository.findByPaciente(any(Paciente.class))).thenReturn(List.of(pacienteAlergia));
        when(pacienteAlergiaMapper.mapToPacienteAlergiaDTO(any())).thenReturn(pacienteAlergiaDTO);

        // Act
        List<PacienteAlergiaDTO> resultado = pacienteAlergiaService.listarPacienteAlergiasPorPaciente(1L);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(pacienteAlergiaRepository, times(1)).findByPaciente(any(Paciente.class));
    }

    @Test
    @DisplayName("Obtener PacienteAlergia por ID existente")
    void getPacienteAlergiaById_existente() {
        // Arrange
        when(pacienteAlergiaRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(pacienteAlergia));
        when(pacienteAlergiaMapper.mapToPacienteAlergiaDTO(any())).thenReturn(pacienteAlergiaDTO);

        // Act
        Optional<PacienteAlergiaDTO> resultado = pacienteAlergiaService.getPacienteAlergiaById(1L);

        // Assert
        assertThat(resultado).isPresent();
        verify(pacienteAlergiaRepository, times(1)).findByIdAndEstadoIsTrue(1L);
    }

    @Test
    @DisplayName("Crear nueva relación Paciente-Alergia")
    void createPacienteAlergia() {
        // Arrange
        PacienteDTO pacienteDTO = new PacienteDTO();
        pacienteDTO.setId(1L);
        pacienteDTO.setNombres("Juan Perez");

        AlergiaDTO alergiaDTO = new AlergiaDTO();
        alergiaDTO.setId(1L);
        alergiaDTO.setNombre("Polen");

        Paciente paciente = new Paciente();
        paciente.setId(1L);

        Alergia alergia = new Alergia();
        alergia.setId(1L);

        when(pacienteService.getPacientePorId(anyLong())).thenReturn(Optional.of(pacienteDTO));
        when(alergiaService.getAlergiaPorId(anyLong())).thenReturn(Optional.of(alergiaDTO));
        when(pacienteMapper.mapToPaciente(any(PacienteDTO.class))).thenReturn(paciente);
        when(alergiaMapper.mapToAlergia(any(AlergiaDTO.class))).thenReturn(alergia);
        when(pacienteAlergiaMapper.mapToPacienteAlergia(any())).thenReturn(pacienteAlergia);
        when(pacienteAlergiaRepository.save(any())).thenReturn(pacienteAlergia);
        when(pacienteAlergiaMapper.mapToPacienteAlergiaDTO(any())).thenReturn(pacienteAlergiaDTO);

        // Act
        PacienteAlergiaDTO resultado = pacienteAlergiaService.createPacienteAlergia(pacienteAlergiaDTO);

        // Assert
        assertThat(resultado).isNotNull();
        verify(pacienteAlergiaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Actualizar Paciente-Alergia existente")
    void updatePacienteAlergia() {
        // Arrange
        when(pacienteAlergiaRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(pacienteAlergia));
        when(pacienteAlergiaRepository.save(any())).thenReturn(pacienteAlergia);
        when(pacienteAlergiaMapper.mapToPacienteAlergiaDTO(any())).thenReturn(pacienteAlergiaDTO);

        // Act
        PacienteAlergiaDTO resultado = pacienteAlergiaService.updatePacienteAlergia(1L, pacienteAlergiaDTO);

        // Assert
        assertThat(resultado).isNotNull();
        verify(pacienteAlergiaRepository, times(1)).findByIdAndEstadoIsTrue(1L);
        verify(pacienteAlergiaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Eliminar Paciente-Alergia existente")
    void deletePacienteAlergia() {
        // Arrange
        when(pacienteAlergiaRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(pacienteAlergia));

        // Act
        pacienteAlergiaService.deletePacienteAlergia(1L);

        // Assert
        assertThat(pacienteAlergia.getEstado()).isFalse();
        verify(pacienteAlergiaRepository, times(1)).save(pacienteAlergia);
    }
}
