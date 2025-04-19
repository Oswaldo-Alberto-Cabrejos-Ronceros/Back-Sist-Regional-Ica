package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.models.AuthenticationResponse;
import com.clinicaregional.clinica.models.LoginRequest;
import com.clinicaregional.clinica.service.AuthenticationService;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsServiceImpl;


    public AuthenticationServiceImpl(JwtUtil jwtUtil, UsuarioService usuarioService, UserDetailsServiceImpl userDetailsServiceImpl){
        this.jwtUtil=jwtUtil;
        this.usuarioService=usuarioService;
        this.passwordEncoder= new BCryptPasswordEncoder();
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }


    @Override
    public AuthenticationResponse authenticateUser(LoginRequest loginRequest) {
        UserDetails userDetails=userDetailsServiceImpl.loadUserByUsername(loginRequest.getEmail());
        System.out.println(userDetails);
        System.out.println(userDetails.getPassword());
        System.out.println(loginRequest.getEmail());
        System.out.println(loginRequest.getPassword());
        if(passwordEncoder.matches(loginRequest.getPassword(),userDetails.getPassword())){
            //generamos token
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(),null,userDetails.getAuthorities());
            String jwtToken=jwtUtil.generateAccessToken(authentication);
            //generamos refresh token
            String refreshToken = jwtUtil.generateRefreshToken(authentication);
            //obtenemos al usuario
            Usuario usuario=usuarioService.obtenerPorCorreo(userDetails.getUsername()).orElseThrow(()->new UsernameNotFoundException("Usuario no encontrado"));
            return new AuthenticationResponse(usuario.getId(),usuario.getNombre(),usuario.getRol().getNombre(),jwtToken,refreshToken);
        } else {
            throw new BadCredentialsException("Credenciales incorrectas");
        }
    }

    @Override
    public String refreshToken(String refreshToken) {
        boolean isValid = jwtUtil.validateToken(refreshToken);
        if(isValid){
            String email = jwtUtil.getEmailFromJwt(refreshToken);
            UserDetails userDetails=userDetailsServiceImpl.loadUserByUsername(email);
            Authentication authentication= new UsernamePasswordAuthenticationToken(userDetails.getUsername(),null,userDetails.getAuthorities());
            return jwtUtil.generateAccessToken(authentication);
        }else {
            throw new JwtException("Error al validad token de refresco");
        }
    }

    @Override
    public AuthenticationResponse registerUser(Usuario usuario) {
        //ponemos el rol de paciente
        Rol rol = new Rol();
        rol.setId(1L);
        usuario.setRol(rol);
        Usuario usuarioregister= usuarioService.guardar(usuario);
        UserDetails userDetails=userDetailsServiceImpl.loadUserByUsername(usuarioregister.getCorreo());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(),null,userDetails.getAuthorities());
        String jwtToken=jwtUtil.generateAccessToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(authentication);
        return new AuthenticationResponse(usuario.getId(),usuario.getNombre(),usuario.getRol().getNombre(),jwtToken,refreshToken);
    }

}
