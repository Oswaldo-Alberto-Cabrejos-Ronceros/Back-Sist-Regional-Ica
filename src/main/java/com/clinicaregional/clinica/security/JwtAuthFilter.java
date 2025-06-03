package com.clinicaregional.clinica.security;

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

    // inyectamos por constructor
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // sobreescribimos el metodo que se encarga de decir en que casos no se aplica
    // el filterInternal
    @Override
    public boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")
                || path.startsWith("/api/auth/refresh");
    }

    // sobreescribimos para aplicar filtro
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                Optional<Cookie> jwtToken = Arrays.stream(cookies)
                        .filter(c -> c.getName().equals("jwtToken"))
                        .findFirst();

                if (jwtToken.isPresent()) {
                    String token = jwtToken.get().getValue();

                    // 🔐 Validar solo si es válido, atrapar cualquier excepción del util
                    if (jwtUtil.validateToken(token)) {
                        String email = jwtUtil.getEmailFromJwt(token);
                        List<GrantedAuthority> authorities = jwtUtil.getAuthoritiesFromJwt(token);

                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email,
                                null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (Exception e) {
            // Evita que excepciones internas del filtro bloqueen rutas públicas
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

}
