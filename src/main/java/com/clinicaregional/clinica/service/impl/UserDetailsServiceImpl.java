package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.util.FiltroEstado;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsuarioService usuarioService;
    private final FiltroEstado filtroEstado;

    @Autowired
    public UserDetailsServiceImpl(UsuarioService usuarioService, FiltroEstado filtroEstado) {
        this.usuarioService = usuarioService;
        this.filtroEstado = filtroEstado;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            filtroEstado.activarFiltroEstado(true);

            log.debug("Buscando usuario con correo: {}", username);
            Usuario usuario = usuarioService.obtenerPorCorreo(username)
                    .orElseThrow(() -> {
                        log.error("Usuario no encontrado con correo: {}", username);
                        return new UsernameNotFoundException("Usuario no encontrado con correo: " + username);
                    });

            log.debug("Usuario encontrado: ID={}, Correo={}", usuario.getId(), usuario.getCorreo());

            // Validaciones detalladas
            if (usuario.getCorreo() == null || usuario.getCorreo().isEmpty()) {
                log.error("El correo del usuario es nulo o vacío");
                throw new IllegalStateException("El correo del usuario no puede ser nulo o vacío");
            }

            if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
                log.error("La contraseña del usuario es nula o vacía. ID Usuario: {}", usuario.getId());
                throw new IllegalStateException("La contraseña del usuario no puede ser nula o vacía");
            }

            if (!usuario.getEstado()) {
                log.error("Usuario inactivo. ID: {}", usuario.getId());
                throw new UsernameNotFoundException("Usuario inactivo");
            }

            if (usuario.getRol() == null) {
                log.error("Usuario no tiene rol asignado. ID: {}", usuario.getId());
                throw new IllegalStateException("Usuario no tiene rol asignado");
            }

            String rol = usuario.getRol().getNombre();
            if (rol == null || rol.isEmpty()) {
                log.error("El nombre del rol es nulo o vacío. ID Rol: {}", usuario.getRol().getId());
                throw new IllegalStateException("El nombre del rol no puede ser nulo o vacío");
            }

            log.debug("Creando UserDetails para usuario {} con rol {}", usuario.getCorreo(), rol);
            return new org.springframework.security.core.userdetails.User(
                    usuario.getCorreo(),
                    usuario.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + rol)));
        } catch (Exception e) {
            log.error("Error en loadUserByUsername para usuario: " + username, e);
            throw e;
        }
    }
}