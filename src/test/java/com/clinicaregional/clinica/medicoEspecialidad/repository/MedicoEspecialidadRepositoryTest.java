package com.clinicaregional.clinica.medicoEspecialidad.repository;

import com.clinicaregional.clinica.entity.*;
import com.clinicaregional.clinica.enums.TipoContrato;
import com.clinicaregional.clinica.enums.TipoMedico;
import com.clinicaregional.clinica.repository.EspecialidadRepository;
import com.clinicaregional.clinica.repository.MedicoEspecialidadRepository;
import com.clinicaregional.clinica.repository.MedicoRepository;
import com.clinicaregional.clinica.repository.RolRepository;
import com.clinicaregional.clinica.repository.TipoDocumentoRepository;
import com.clinicaregional.clinica.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class MedicoEspecialidadRepositoryTest {

    @Autowired
    private MedicoEspecialidadRepository medicoEspecialidadRepository;
    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private EspecialidadRepository especialidadRepository;
    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;

    private Medico medico;
    private Especialidad especialidad;

    @BeforeEach
    void setUp() {
        // Guardar TipoDocumento
        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setNombre("DNI");
        tipoDocumento.setDescripcion("Documento Nacional");
        tipoDocumento.setEstado(true);
        tipoDocumento = tipoDocumentoRepository.save(tipoDocumento);

        Rol rol = new Rol();
        rol.setNombre("ROLE_MEDICO");
        rol.setDescripcion("Rol para médicos");
        rol.setEstado(true);
        rol = rolRepository.save(rol);

        // Guardar Usuario
        Usuario usuario = new Usuario();
        usuario.setCorreo("medico@example.com");
        usuario.setPassword("securepass");
        usuario.setRol(rol);
        usuario.setEstado(true);                                                                        
        usuario = usuarioRepository.save(usuario);

        // Guardar Especialidad
        especialidad = new Especialidad();
        especialidad.setNombre("Cardiología");
        especialidad.setDescripcion("Especialidad del corazón");
        especialidad.setImagen("cardio.png");
        especialidad.setEstado(true);
        especialidad = especialidadRepository.save(especialidad);

        // Guardar Medico
        medico = new Medico();
        medico.setNombres("Luis");
        medico.setApellidos("Ramirez");
        medico.setNumeroColegiatura("12345678901");
        medico.setNumeroRNE("987654321");
        medico.setNumeroDocumento("76543210");
        medico.setTelefono("987654321");
        medico.setDireccion("Calle Falsa 123");
        medico.setDescripcion("Médico general");
        medico.setImagen("luis.png");
        medico.setFechaContratacion(LocalDateTime.now());
        medico.setTipoContrato(TipoContrato.FIJO);
        medico.setTipoMedico(TipoMedico.ESPECIALISTA);
        medico.setTipoDocumento(tipoDocumento);
        medico.setUsuario(usuario);
        medico.setEstado(true);
        medico = medicoRepository.save(medico);

        // Guardar relación
        MedicoEspecialidad me = new MedicoEspecialidad();
        me.setId(new MedicoEspecialidadId(medico.getId(), especialidad.getId()));
        me.setMedico(medico);
        me.setEspecialidad(especialidad);
        me.setDesdeFecha(LocalDate.now());
        me.setEstado(true);

        medicoEspecialidadRepository.save(me);
    }

    @Test
    @DisplayName("Buscar relación por ID y estado debe retornar relación")
    void findByIdAndEstadoIsTrue_debeRetornarRelacion() {
        MedicoEspecialidadId id = new MedicoEspecialidadId(medico.getId(), especialidad.getId());
        Optional<MedicoEspecialidad> resultado = medicoEspecialidadRepository.findByIdAndEstadoIsTrue(id);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getMedico().getNombres()).isEqualTo("Luis");
    }

    @Test
    @DisplayName("Buscar relaciones por ID de médico")
    void findByMedicoId_debeRetornarLista() {
        List<MedicoEspecialidad> resultado = medicoEspecialidadRepository.findByMedicoId(medico.getId());
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Buscar relaciones por ID de especialidad")
    void findByEspecialidadId_debeRetornarLista() {
        List<MedicoEspecialidad> resultado = medicoEspecialidadRepository.findByEspecialidadId(especialidad.getId());
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Validar existencia por médico y especialidad")
    void existsByMedicoAndEspecialidad_debeRetornarTrue() {
        boolean existe = medicoEspecialidadRepository.existsByMedicoAndEspecialidad(medico, especialidad);
        assertThat(existe).isTrue();
    }
}
