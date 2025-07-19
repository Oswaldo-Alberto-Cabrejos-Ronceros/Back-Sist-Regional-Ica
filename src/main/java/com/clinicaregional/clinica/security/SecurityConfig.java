package com.clinicaregional.clinica.security;

import com.clinicaregional.clinica.service.AuthenticationService;
import com.clinicaregional.clinica.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtUtil jwtUtil;

    @Autowired
    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider)
            throws Exception {
        http.authenticationProvider(authenticationProvider);
        return http.csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(httpRequest -> {
                    httpRequest
                            .requestMatchers(
                                    "/api/auth/**",
                                    "/swagger-ui/**",
                                    "/api/**", //quitar en produccion
                                    "/v3/api-docs/**",
                                    "/api-docs/**",
                                    "/swagger-resources/**",
                                    "/webjars/**")
                            .permitAll()
                            .requestMatchers("/api/administradores/**").hasAuthority("ADMIN")
                            .requestMatchers("/api/alergias/**").hasAnyAuthority("ADMIN", "MEDICO", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.POST, "/api/citas").hasAnyAuthority("PACIENTE", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.GET, "/api/citas").hasAnyAuthority("MEDICO", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.GET, "/api/citas/citas-medico/**")
                            .hasAnyAuthority("MEDICO", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.PUT, "/api/citas/confirmar/**").hasAuthority("MEDICO")
                            .requestMatchers(HttpMethod.PUT, "/api/citas/atender/**").hasAuthority("MEDICO")
                            .requestMatchers(HttpMethod.PUT, "/api/citas/reprogramar/**")
                            .hasAnyAuthority("MEDICO", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.GET, "/api/citas/medico/*/pacientes")
                            .hasAnyAuthority("MEDICO", "RECEPCIONISTA", "ADMIN")
                            .requestMatchers(HttpMethod.GET, "/api/citas/paciente/*/citas-futuras")
                            .hasAuthority("PACIENTE")
                            .requestMatchers(HttpMethod.PUT, "/api/citas/*")
                            .hasAnyAuthority("PACIENTE", "MEDICO", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.DELETE, "/api/citas/*")
                            .hasAnyAuthority("PACIENTE", "MEDICO", "RECEPCIONISTA")

                            .requestMatchers("/api/coberturas/**").hasAuthority("ADMIN")
                            .requestMatchers("/api/disponibilidad/**")
                            .hasAnyAuthority("ADMIN", "MEDICO", "RECEPCIONISTA")
                            .requestMatchers("/api/especialidades/**").hasAuthority("ADMIN")
                            .requestMatchers("/api/horario-bloques/**")
                            .hasAnyAuthority("ADMIN", "MEDICO", "RECEPCIONISTA")

                            .requestMatchers("/api/medicos/**").authenticated()

                            .requestMatchers("/api/medico-especialidad/**").authenticated()
                            .requestMatchers("/api/paciente-alergia/**").authenticated()
                            .requestMatchers("/api/pacientes/**").authenticated()
                            .requestMatchers("/api/recepcionistas/**").hasAuthority("ADMIN")
                            .requestMatchers("/api/roles/**").hasAuthority("ADMIN")
                            .requestMatchers("/api/seguro-coberturas/**").authenticated()
                            .requestMatchers("/api/seguros/**").hasAuthority("ADMIN")
                            .requestMatchers("/api/servicios/**").authenticated()
                            .requestMatchers("/api/servicios-seguros/**").authenticated()
                            .requestMatchers("/api/tipos-documentos/**").authenticated()
                            .requestMatchers("/api/usuarios/**").hasAuthority("ADMIN")

                            .anyRequest().authenticated();
                })
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\": \"UNAUTHORIZED\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("{\"error\": \"FORBIDDEN\"}");
                        }))
                .addFilterBefore(new JwtAuthFilter(jwtUtil), BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(AuthenticationService authenticationService,
            UserDetailsServiceImpl userDetailsServiceImpl) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsServiceImpl::loadUserByUsername);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://localhost:5173",
                "https://clinica-regional-ica.vercel.app",
                "https://clinica-regional-ica-git-qa-alyri03s-projects.vercel.app",
                "https://clinica-regional-ica-git-develop-alyri03s-projects.vercel.app",
                "https://backend-dev-desarrollo.up.railway.app",
                "https://luminous-flow-staging-qa.up.railway.app",
                "https://back-sist-regional-ica-production.up.railway.app"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
