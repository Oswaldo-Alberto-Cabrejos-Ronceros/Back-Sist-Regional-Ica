package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsuarioService usuarioService;
    @Autowired
    public UserDetailsServiceImpl(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.obtenerPorCorreo(username).orElseThrow(()->new UsernameNotFoundException("Usuario no encontrado"));
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(usuario.getRol().getNombre()));
        return new org.springframework.security.core.userdetails.User(
                usuario.getCorreo(), usuario.getContraseña(), authorities
        );
    }
}
