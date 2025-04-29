package com.clinicaregional.clinica.rol.service;

import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.mapper.RolMapper;
import com.clinicaregional.clinica.repository.RolRepository;
import com.clinicaregional.clinica.service.impl.RolServiceImpl;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @Mock
    private RolMapper rolMapper;

    @Mock
    private FiltroEstado filtroEstado;

    @InjectMocks
    private RolServiceImpl rolService;

    private Rol rol;
    private RolDTO rolDTO;

    @BeforeEach
    void setUp() {
        rol = new Rol(1L, "ADMIN", "Administrador del sistema");
        rolDTO = new RolDTO(1L, "ADMIN", "Administrador del sistema");

        doNothing().when(filtroEstado).activarFiltroEstado(true);
    }

    @Test
    void listarRoles_exitoso() {
        when(rolRepository.findAll()).thenReturn(List.of(rol));
        when(rolMapper.mapToRolDTO(any(Rol.class))).thenReturn(rolDTO);

        List<RolDTO> roles = rolService.listarRoles();

        assertThat(roles).hasSize(1);
        assertThat(roles.get(0).getNombre()).isEqualTo("ADMIN");
        verify(rolRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_existente() {
        when(rolRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(rol));
        when(rolMapper.mapToRolDTO(any(Rol.class))).thenReturn(rolDTO);

        Optional<RolDTO> result = rolService.obtenerPorId(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getNombre()).isEqualTo("ADMIN");
    }

    @Test
    void obtenerPorId_noExistente() {
        when(rolRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        Optional<RolDTO> result = rolService.obtenerPorId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void guardarRol_exitoso() {
        when(rolRepository.existsByNombreAndEstadoIsTrue(anyString())).thenReturn(false);
        when(rolMapper.mapToRol(any(RolDTO.class))).thenReturn(rol);
        when(rolRepository.save(any(Rol.class))).thenReturn(rol);
        when(rolMapper.mapToRolDTO(any(Rol.class))).thenReturn(rolDTO);

        RolDTO result = rolService.guardar(rolDTO);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("ADMIN");
        verify(rolRepository, times(1)).save(any(Rol.class));
    }

    @Test
    void guardarRol_conNombreExistente_deberiaLanzarExcepcion() {
        when(rolRepository.existsByNombreAndEstadoIsTrue(anyString())).thenReturn(true);

        assertThatThrownBy(() -> rolService.guardar(rolDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El nombre ya existe");
    }

    @Test
    void actualizarRol_exitoso() {
        when(rolRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(rol));
        when(rolRepository.existsByNombreAndEstadoIsTrue(anyString())).thenReturn(false);
        when(rolRepository.save(any(Rol.class))).thenReturn(rol);
        when(rolMapper.mapToRolDTO(any(Rol.class))).thenReturn(rolDTO);

        RolDTO actualizado = rolService.actualizar(1L, rolDTO);

        assertThat(actualizado.getNombre()).isEqualTo("ADMIN");
        verify(rolRepository, times(1)).save(any(Rol.class));
    }

    @Test
    void actualizarRol_noExistente_deberiaLanzarExcepcion() {
        when(rolRepository.findByIdAndEstadoIsTrue(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rolService.actualizar(99L, rolDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe un rol con el id");
    }

    @Test
    void eliminarRol_exitoso() {
        Rol rolExistente = new Rol(1L, "ADMIN", "Administrador del sistema");
        rolExistente.setEstado(true);

        when(rolRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(rolExistente));

        rolService.eliminar(1L);

        assertThat(rolExistente.getEstado()).isFalse();
        verify(rolRepository, times(1)).save(rolExistente);
    }
}
