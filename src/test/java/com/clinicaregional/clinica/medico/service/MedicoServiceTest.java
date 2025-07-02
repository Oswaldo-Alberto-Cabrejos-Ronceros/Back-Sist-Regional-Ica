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
import com.clinicaregional.clinica.mapper.MedicoMapper;
import com.clinicaregional.clinica.repository.MedicoRepository;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.service.TipoDocumentoService;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.service.impl.MedicoServiceImpl;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MedicoServiceTest {

    @Mock private MedicoRepository medicoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private UsuarioService usuarioService;
    @Mock private TipoDocumentoService tipoDocumentoService;
    @Mock private MedicoMapper medicoMapper;
    @Mock private FiltroEstado filtroEstado;

    @InjectMocks private MedicoServiceImpl medicoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerMedicos_debeRetornarLista() {
        Medico medico = Medico.builder().id(1L).nombres("Carlos").estado(true).build();
        MedicoResponseDTO dto = new MedicoResponseDTO(); dto.setId(1L); dto.setNombres("Carlos");
        when(medicoRepository.findAll()).thenReturn(List.of(medico));
        when(medicoMapper.mapToMedicoResponseDTO(medico)).thenReturn(dto);

        List<MedicoResponseDTO> lista = medicoService.obtenerMedicos();

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getNombres()).isEqualTo("Carlos");
    }

    @Test
    void obtenerMedicosPublic_debeRetornarLista() {
        Medico medico = Medico.builder().id(1L).nombres("Eva").estado(true).build();
        MedicoResponsePublicDTO dto = new MedicoResponsePublicDTO(); dto.setId(1L); dto.setNombres("Eva");
        when(medicoRepository.findAll()).thenReturn(List.of(medico));
        when(medicoMapper.mapToMedicoResponsePublicDTO(medico)).thenReturn(dto);

        List<MedicoResponsePublicDTO> lista = medicoService.obtenerMedicosPublic();

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getNombres()).isEqualTo("Eva");
    }

    @Test
    void obtenerMedicoPorId_idNoExiste_debeLanzarExcepcion() {
        when(medicoRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicoService.obtenerMedicoPorId(99L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Medico no encontrado");
    }

    @Test
    void actualizarMedico_idNoExiste_debeLanzarExcepcion() {
        when(medicoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicoService.actualizarMedico(1L, new MedicoRequestDTO()))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Médico no encontrado");
    }

    // @Test
    // void eliminarMedico_idNoExiste_debeLanzarExcepcion() {
    //     when(medicoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.empty());

    //     assertThatThrownBy(() -> medicoService.eliminarMedico(1L))
    //         .isInstanceOf(RuntimeException.class)
    //         .hasMessageContaining("Medico no encontrado");
    // }

    // @Test
    // void eliminarMedico_usuarioNoExiste_debeLanzarExcepcion() {
    //     Medico medico = Medico.builder().id(1L).estado(true)
    //         .usuario(Usuario.builder().id(2L).estado(true).build()).build();
    //     when(medicoRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(medico));
    //     when(usuarioRepository.findByIdAndEstadoIsTrue(2L)).thenReturn(Optional.empty());

    //     assertThatThrownBy(() -> medicoService.eliminarMedico(1L))
    //         .isInstanceOf(RuntimeException.class)
    //         .hasMessageContaining("Usuario no encontrado");
    // }
}
