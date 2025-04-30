package com.clinicaregional.clinica.usuarios.repository;

import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.repository.RolRepository;
import com.clinicaregional.clinica.repository.UsuarioRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository; // necesitas un Rol para el Usuario

    @Test
    @DisplayName("Guardar y buscar usuario por correo")
    void testGuardarYBuscarPorCorreo() {
        Rol rol = new Rol();
        rol.setNombre("PACIENTE");
        rol.setDescripcion("Paciente registrado");
        rol.setEstado(true);
        rol = rolRepository.save(rol);

        Usuario usuario = new Usuario();
        usuario.setCorreo("test@correo.com");
        usuario.setPassword("password123");
        usuario.setEstado(true);
        usuario.setRol(rol);

        usuarioRepository.save(usuario);

        Optional<Usuario> encontrado = usuarioRepository.findByCorreo("test@correo.com");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getCorreo()).isEqualTo("test@correo.com");
    }

    @Test
    @DisplayName("Verificar existencia por correo")
    void testExistePorCorreo() {
        Rol rol = new Rol();
        rol.setNombre("ADMIN");
        rol.setDescripcion("Administrador");
        rol.setEstado(true);
        rol = rolRepository.save(rol);

        Usuario usuario = new Usuario();
        usuario.setCorreo("admin@correo.com");
        usuario.setPassword("adminpass");
        usuario.setEstado(true);
        usuario.setRol(rol);

        usuarioRepository.save(usuario);

        boolean existe = usuarioRepository.existsByCorreo("admin@correo.com");

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Listar usuarios por ID de Rol")
    void testListarPorRolId() {
        Rol rol = new Rol();
        rol.setNombre("RECEPCIONISTA");
        rol.setDescripcion("Recepcionista de la clínica");
        rol.setEstado(true);
        rol = rolRepository.save(rol);

        Usuario usuario1 = new Usuario();
        usuario1.setCorreo("recep1@correo.com");
        usuario1.setPassword("recep1pass");
        usuario1.setEstado(true);
        usuario1.setRol(rol);

        Usuario usuario2 = new Usuario();
        usuario2.setCorreo("recep2@correo.com");
        usuario2.setPassword("recep2pass");
        usuario2.setEstado(true);
        usuario2.setRol(rol);

        usuarioRepository.saveAll(List.of(usuario1, usuario2));

        List<Usuario> usuarios = usuarioRepository.findByRol_Id(rol.getId());

        assertThat(usuarios).hasSize(2);
    }

    @Test
    @DisplayName("Buscar usuario por ID y estado activo")
    void testBuscarPorIdYEstadoActivo() {
        Rol rol = new Rol();
        rol.setNombre("MEDICO");
        rol.setDescripcion("Médico registrado");
        rol.setEstado(true);
        rol = rolRepository.save(rol);

        Usuario usuario = new Usuario();
        usuario.setCorreo("medico@correo.com");
        usuario.setPassword("medicopass");
        usuario.setEstado(true);
        usuario.setRol(rol);

        Usuario guardado = usuarioRepository.save(usuario);

        Optional<Usuario> encontrado = usuarioRepository.findByIdAndEstadoIsTrue(guardado.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getCorreo()).isEqualTo("medico@correo.com");
    }
}
