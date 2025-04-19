package com.clinicaregional.clinica.config;

import com.clinicaregional.clinica.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    //inyectamos por constructor
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    //sobreescribimos el metodo que se encarga de decir en que casos no se aplica el filterInternal
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register") || path.startsWith("/api/auth/refresh");
    }

    //sobreescribimos para aplicar filtro
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //obtenemos las cookies
        Cookie[] cookies = request.getCookies();
        //si existen cookies
        if (cookies != null) {
            //buscamos la cookie jwtToken
            Optional<Cookie> jwtToken = Arrays.stream(cookies).filter(c -> c.getName().equals("jwtToken")).findFirst();
            if (jwtToken.isPresent()) {
                String token = jwtToken.get().getValue();
                //validadamos token
                if (jwtUtil.validateToken(token)) {
                    String email = jwtUtil.getEmailFromJwt(token);
                    List<GrantedAuthority> authorities = jwtUtil.getAuthoritiesFromJwt(token);
                    //creamos un usuario autenticado
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, null, authorities);
                    //guardamos el usuario autenticado en el contexto de spring security
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
