package com.clinicaregional.clinica.medico.service;

import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.MedicoRequestDTO;
import com.clinicaregional.clinica.dto.request.UsuarioRequestDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponseDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponsePublicDTO;
import com.clinicaregional.clinica.entity.*;
import com.clinicaregional.clinica.enums.TipoContrato;
import com.clinicaregional.clinica.enums.TipoMedico;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.MedicoMapper;
import com.clinicaregional.clinica.repository.MedicoRepository;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.service.TipoDocumentoService;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.service.impl.MedicoServiceImpl;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MedicoServiceImplTest {

    @Mock
    private MedicoRepository medicoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private TipoDocumentoService tipoDocumentoService;
    @Mock
    private MedicoMapper medicoMapper;
    @Mock
    private FiltroEstado filtroEstado;

    @InjectMocks
    private MedicoServiceImpl medicoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Obtener médico por ID - exitoso")
    void obtenerMedicoPorId_existente_debeRetornarDTO() {
        Medico medico = Medico.builder().id(1L).nombres("Luis").estado(true).build();
        MedicoResponseDTO dto = new MedicoResponseDTO();
        dto.setId(1L);
        dto.setNombres("Luis");

        when(medicoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(medico));
        when(medicoMapper.mapToMedicoResponseDTO(medico)).thenReturn(dto);

        MedicoResponseDTO resultado = medicoService.obtenerMedicoPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombres()).isEqualTo("Luis");
    }

    @Test
    @DisplayName("Obtener médico por ID - inexistente")
    void obtenerMedicoPorId_inexistente_debeLanzarExcepcion() {
        when(medicoRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicoService.obtenerMedicoPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Medico no encontrado");
    }

    @Test
    @DisplayName("Guardar médico - exitoso")
    void guardarMedico_debeGuardarYRetornarDTO() {
        MedicoRequestDTO dto = new MedicoRequestDTO();
        dto.setNombres("Carla");
        dto.setApellidos("Ramos");
        dto.setNumeroColegiatura("00011122233");
        dto.setNumeroRNE("998877665");
        dto.setTipoDocumentoId(1L);
        dto.setNumeroDocumento("12345678");
        dto.setTelefono("987654321");
        dto.setDireccion("Av. Salud 123");
        dto.setDescripcion("Especialista en cardiología");
        dto.setImagen("img.png");
        dto.setFechaContratacion(LocalDateTime.now());
        dto.setTipoContrato(TipoContrato.FIJO);
        dto.setTipoMedico(TipoMedico.ESPECIALISTA);
        dto.setCorreo("carla@example.com");
        dto.setPassword("pass1234");

        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setId(1L);

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1L);

        Medico medico = Medico.builder().id(1L).nombres("Carla").estado(true).build();
        MedicoResponseDTO responseDTO = new MedicoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNombres("Carla");

        when(medicoRepository.existsByNumeroColegiatura(dto.getNumeroColegiatura())).thenReturn(false);
        when(medicoRepository.existsByNumeroRNE(dto.getNumeroRNE())).thenReturn(false);
        when(medicoRepository.existsByNumeroDocumento(dto.getNumeroDocumento())).thenReturn(false);
        when(usuarioRepository.existsByCorreo(dto.getCorreo())).thenReturn(false);
        when(tipoDocumentoService.getTipoDocumentoByIdContext(1L)).thenReturn(Optional.of(tipoDocumento));
        when(usuarioService.guardar(any(UsuarioRequestDTO.class))).thenReturn(usuarioDTO);
        when(medicoRepository.save(any(Medico.class))).thenReturn(medico);
        when(medicoMapper.mapToMedicoResponseDTO(medico)).thenReturn(responseDTO);

        MedicoResponseDTO result = medicoService.guardarMedico(dto);

        assertThat(result).isNotNull();
        assertThat(result.getNombres()).isEqualTo("Carla");
    }

    @Test
    @DisplayName("Guardar médico con colegiatura duplicada debe lanzar excepción")
    void guardarMedico_conColegiaturaExistente_debeLanzarExcepcion() {
        MedicoRequestDTO dto = new MedicoRequestDTO();
        dto.setNumeroColegiatura("00011122233");

        when(medicoRepository.existsByNumeroColegiatura("00011122233")).thenReturn(true);

        assertThatThrownBy(() -> medicoService.guardarMedico(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un médico con el número de colegiatura");
    }

    @Test
    @DisplayName("Actualizar médico - exitoso")
    void actualizarMedico_debeActualizarYRetornarDTO() {
        Long id = 1L;

        MedicoRequestDTO dto = new MedicoRequestDTO();
        dto.setNombres("Actualizado");
        dto.setApellidos("Perez");
        dto.setNumeroColegiatura("99988877766");
        dto.setNumeroRNE("123456789");
        dto.setTipoDocumentoId(1L);
        dto.setNumeroDocumento("65432100");
        dto.setTelefono("987654321");
        dto.setDireccion("Av. Actualizada 456");
        dto.setDescripcion("Cardiólogo con experiencia");
        dto.setImagen("nuevo.png");
        dto.setFechaContratacion(LocalDateTime.now());
        dto.setTipoContrato(TipoContrato.FIJO);
        dto.setTipoMedico(TipoMedico.ESPECIALISTA);
        dto.setCorreo("actualizado@example.com");
        dto.setPassword("nuevaClave123");

        Usuario usuario = Usuario.builder()
                .id(2L)
                .correo("viejo@example.com")
                .estado(true)
                .build();

        Medico medico = Medico.builder()
                .id(id)
                .numeroColegiatura("00000000000")
                .numeroRNE("987654321")
                .usuario(usuario)
                .estado(true)
                .build();

        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setId(1L);

        MedicoResponseDTO expected = new MedicoResponseDTO();
        expected.setId(id);
        expected.setNombres("Actualizado");

        when(medicoRepository.findByIdAndEstadoIsTrue(id)).thenReturn(Optional.of(medico));
        when(usuarioRepository.findByIdAndEstadoIsTrue(2L)).thenReturn(Optional.of(usuario));
        when(tipoDocumentoService.getTipoDocumentoByIdContext(1L)).thenReturn(Optional.of(tipoDocumento));
        when(medicoRepository.existsByNumeroColegiatura(dto.getNumeroColegiatura())).thenReturn(false);
        when(medicoRepository.existsByNumeroRNE(dto.getNumeroRNE())).thenReturn(false);
        when(medicoRepository.existsByNumeroDocumento(dto.getNumeroDocumento())).thenReturn(false);
        when(medicoRepository.existsByUsuario(usuario)).thenReturn(false);
        when(medicoRepository.findByUsuarioCorreo(dto.getCorreo())).thenReturn(Optional.empty());
        when(medicoRepository.save(any())).thenReturn(medico);
        when(medicoMapper.mapToMedicoResponseDTO(medico)).thenReturn(expected);

        MedicoResponseDTO result = medicoService.actualizarMedico(id, dto);

        assertThat(result).isNotNull();
        assertThat(result.getNombres()).isEqualTo("Actualizado");
    }

    @Test
    @DisplayName("Eliminar médico - exitoso")
    void eliminarMedico_debeCambiarEstado() {
        Medico medico = Medico.builder().id(1L).estado(true).usuario(Usuario.builder().id(2L).build()).build();
        Usuario usuario = Usuario.builder().id(2L).estado(true).build();

        when(medicoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(medico));
        when(usuarioRepository.findByIdAndEstadoIsTrue(2L)).thenReturn(Optional.of(usuario));

        medicoService.eliminarMedico(1L);

        verify(medicoRepository).save(any());
        verify(usuarioRepository).save(any());
        assertThat(medico.getEstado()).isFalse();
        assertThat(usuario.getEstado()).isFalse();
    }
}