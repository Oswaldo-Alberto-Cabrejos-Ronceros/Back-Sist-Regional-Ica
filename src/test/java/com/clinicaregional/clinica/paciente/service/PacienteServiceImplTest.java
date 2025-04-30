package com.clinicaregional.clinica.paciente.service;

import com.clinicaregional.clinica.dto.PacienteDTO;
import com.clinicaregional.clinica.dto.TipoDocumentoDTO;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.enums.Sexo;
import com.clinicaregional.clinica.enums.TipoSangre;
import com.clinicaregional.clinica.mapper.PacienteMapper;
import com.clinicaregional.clinica.repository.PacienteRepository;
import com.clinicaregional.clinica.service.TipoDocumentoService;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.service.impl.PacienteServiceImpl;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class PacienteServiceImplTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private PacienteMapper pacienteMapper;

    @Mock
    private FiltroEstado filtroEstado;

    @Mock
    private TipoDocumentoService tipoDocumentoService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private PacienteServiceImpl pacienteService;

    private Paciente paciente;
    private PacienteDTO pacienteDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        TipoDocumentoDTO tipoDocumentoDTO = new TipoDocumentoDTO(1L, "DNI", "Documento");

        paciente = Paciente.builder()
                .id(1L)
                .nombres("Juan")
                .apellidos("Pérez")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .sexo(Sexo.MASCULINO)
                .numeroIdentificacion("12345678")
                .nacionalidad("PERUANA")
                .telefono("987654321")
                .direccion("Calle Falsa 123")
                .tipoSangre(TipoSangre.O_POSITIVO)
                .estado(true)
                .build();

        pacienteDTO = new PacienteDTO();
        pacienteDTO.setId(1L);
        pacienteDTO.setNombres("Juan");
        pacienteDTO.setApellidos("Pérez");
        pacienteDTO.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        pacienteDTO.setSexo(Sexo.MASCULINO);
        pacienteDTO.setNumeroIdentificacion("12345678");
        pacienteDTO.setNacionalidad("PERUANA");
        pacienteDTO.setTelefono("987654321");
        pacienteDTO.setDireccion("Calle Falsa 123");
        pacienteDTO.setTipoSangre(TipoSangre.O_POSITIVO);
        pacienteDTO.setTipoDocumento(tipoDocumentoDTO);
    }

    @Test
    @DisplayName("Listar todos los pacientes")
    void listarPacientes() {
        // Arrange
        when(pacienteRepository.findAll()).thenReturn(List.of(paciente));
        when(pacienteMapper.mapToPacienteDTO(any(Paciente.class))).thenReturn(pacienteDTO);

        // Act
        List<PacienteDTO> resultado = pacienteService.listarPacientes();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombres()).isEqualTo("Juan");
        verify(pacienteRepository).findAll();
    }

    @Test
    @DisplayName("Obtener paciente por ID")
    void getPacientePorId() {
        // Arrange
        when(pacienteRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(paciente));
        when(pacienteMapper.mapToPacienteDTO(any(Paciente.class))).thenReturn(pacienteDTO);

        // Act
        Optional<PacienteDTO> resultado = pacienteService.getPacientePorId(1L);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        verify(pacienteRepository).findByIdAndEstadoIsTrue(1L);
    }

    @Test
    @DisplayName("Obtener paciente por identificación")
    void getPacientePorIdentificacion() {
        // Arrange
        when(pacienteRepository.findByNumeroIdentificacion("12345678")).thenReturn(Optional.of(paciente));
        when(pacienteMapper.mapToPacienteDTO(any(Paciente.class))).thenReturn(pacienteDTO);

        // Act
        Optional<PacienteDTO> resultado = pacienteService.getPacientePorIdentificacion("12345678");

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNumeroIdentificacion()).isEqualTo("12345678");
        verify(pacienteRepository).findByNumeroIdentificacion("12345678");
    }

    @Test
    @DisplayName("Crear paciente correctamente")
    void crearPaciente() {
        // Arrange
        TipoDocumento tipoDocumento = TipoDocumento.builder()
                .id(1L)
                .nombre("DNI")
                .descripcion("Documento Nacional")
                .estado(true)
                .build();
        when(tipoDocumentoService.getTipoDocumentoByIdContext(anyLong())).thenReturn(Optional.of(tipoDocumento));
        when(pacienteMapper.mapToPaciente(any(PacienteDTO.class))).thenReturn(paciente);
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);
        when(pacienteMapper.mapToPacienteDTO(any(Paciente.class))).thenReturn(pacienteDTO);

        // Act
        PacienteDTO resultado = pacienteService.crearPaciente(pacienteDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombres()).isEqualTo("Juan");
        verify(pacienteRepository).save(any(Paciente.class));
    }

    @Test
    @DisplayName("Actualizar paciente correctamente")
    void actualizarPaciente() {
        // Arrange
        when(pacienteRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(paciente));
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);
        when(pacienteMapper.mapToPacienteDTO(any(Paciente.class))).thenReturn(pacienteDTO);

        // Act
        PacienteDTO resultado = pacienteService.actualizarPaciente(1L, pacienteDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(pacienteRepository).save(any(Paciente.class));
    }

    @Test
    @DisplayName("Eliminar paciente correctamente")
    void eliminarPaciente() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(99L);
        paciente.setUsuario(usuario);
        when(pacienteRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(paciente));

        // Act
        pacienteService.eliminarPaciente(1L);

        // Assert
        assertThat(paciente.getEstado()).isFalse();
        verify(usuarioService).eliminar(99L);
        verify(pacienteRepository).save(paciente);
    }
}
