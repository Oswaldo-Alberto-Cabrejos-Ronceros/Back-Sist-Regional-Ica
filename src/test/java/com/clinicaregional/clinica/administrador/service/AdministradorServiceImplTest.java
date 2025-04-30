package com.clinicaregional.clinica.administrador.service;

import com.clinicaregional.clinica.dto.AdministradorDTO;
import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.RegisterAdministradorRequest;
import com.clinicaregional.clinica.dto.request.UsuarioRequestDTO;
import com.clinicaregional.clinica.entity.Administrador;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.mapper.AdministradorMapper;
import com.clinicaregional.clinica.repository.AdministradorRepository;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.service.impl.AdministradorServiceImpl;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdministradorServiceImplTest {

    @Mock
    private AdministradorRepository administradorRepository;

    @Mock
    private AdministradorMapper administradorMapper;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private FiltroEstado filtroEstado;

    @InjectMocks
    private AdministradorServiceImpl administradorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Listar administradores activos")
    void listarAdministradores_debeRetornarLista() {
        Administrador admin = new Administrador();
        admin.setId(1L);
        when(administradorRepository.findAll()).thenReturn(List.of(admin));
        when(administradorMapper.mapToAdministradorDTO(admin)).thenReturn(new AdministradorDTO());

        List<AdministradorDTO> resultado = administradorService.listarAdministradores();

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Obtener administrador por ID")
    void getAdministradorById_existente() {
        Administrador admin = new Administrador();
        admin.setId(1L);
        when(administradorRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(admin));
        when(administradorMapper.mapToAdministradorDTO(admin)).thenReturn(
                new AdministradorDTO(1L, "Nombre", "Apellido", "123", 1L, "999", "Calle", LocalDate.now(), 1L));

        Optional<AdministradorDTO> resultado = administradorService.getAdministradorById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Crear nuevo administrador")
    void createAdministrador_nuevo() {
        RegisterAdministradorRequest request = new RegisterAdministradorRequest();
        AdministradorDTO adminDTO = new AdministradorDTO(null, "Nombre", "Apellido", "999", 1L, "999", "Dirección", LocalDate.now(), null);
        RolDTO rolDTO = new RolDTO(2L, "ADMINISTRADOR");
        UsuarioDTO usuarioDTO = new UsuarioDTO(1L, "correo@x.com", rolDTO);

        request.setAdministrador(adminDTO);
        request.setUsuario(new UsuarioRequestDTO("correo@x.com", "123456", true, rolDTO));

        when(administradorRepository.existsByNumeroDocumento("999")).thenReturn(false);
        when(usuarioService.guardar(any())).thenReturn(usuarioDTO);
        when(administradorMapper.mapToAdministrador(any())).thenReturn(new Administrador());
        when(administradorRepository.save(any())).thenReturn(new Administrador());
        when(administradorMapper.mapToAdministradorDTO(any())).thenReturn(new AdministradorDTO());

        AdministradorDTO resultado = administradorService.createAdministrador(request);

        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("Eliminar administrador existente")
    void deleteAdministrador_existente() {
        Administrador admin = new Administrador();
        Usuario user = new Usuario();
        user.setId(1L);
        admin.setId(1L);
        admin.setUsuario(user);
        when(administradorRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(admin));

        administradorService.deleteAdministrador(1L);

        verify(usuarioService).eliminar(1L);
        verify(administradorRepository).save(admin);
        assertThat(admin.getEstado()).isFalse();
    }

    @Test
    @DisplayName("Actualizar administrador debe lanzar excepción si ya existe documento")
    void updateAdministrador_documentoDuplicado() {
        AdministradorDTO dto = new AdministradorDTO(1L, "N", "A", "999", 1L, "999", "D", LocalDate.now(), 1L);
        Administrador existente = new Administrador();
        existente.setId(1L);
        when(administradorRepository.findByIdAndEstadoIsTrue(1L)).thenReturn(Optional.of(existente));
        when(administradorRepository.existsByNumeroDocumento("999")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> administradorService.updateAdministrador(1L, dto));
    }
} 
