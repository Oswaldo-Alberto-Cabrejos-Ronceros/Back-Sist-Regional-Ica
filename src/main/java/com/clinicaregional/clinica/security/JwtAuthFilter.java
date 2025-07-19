package com.clinicaregional.clinica.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
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
        System.out.println("JwtAuthFilter ejecutado");

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
            if (cookies == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No authentication token found");
                return;
            }

            Optional<Cookie> jwtToken = Arrays.stream(cookies)
                    .filter(c -> c.getName().equals("jwtToken"))
                    .findFirst();

            if (jwtToken.isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token not found");
                return;
            }

            String token = jwtToken.get().getValue();

            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmailFromJwt(token);
                List<GrantedAuthority> authorities = jwtUtil.getAuthoritiesFromJwt(token);

                log.debug("Authenticating user: {} with authorities: {}", email, authorities);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, null,
                        authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            log.error("Error in JWT filter", e);
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid authentication");
            return;
        }

        filterChain.doFilter(request, response);
    }

}
