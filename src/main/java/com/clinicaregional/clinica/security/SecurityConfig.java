package com.clinicaregional.clinica.security;

import com.clinicaregional.clinica.service.AuthenticationService;
import com.clinicaregional.clinica.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.http.HttpMethod;
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
                    httpRequest.requestMatchers(
                            "/api/auth/**",
                            "/api/**",
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/api-docs/**",
                            "/swagger-resources/**",
                            "/webjars/**").permitAll()
                            .requestMatchers("/api/administradores/**").hasRole("ADMINISTRADOR")
                            .requestMatchers("/api/alergias/**").hasAnyRole("ADMINISTRADOR", "MEDICO", "RECEPCIONISTA")

                            .requestMatchers(HttpMethod.POST, "/api/citas").hasAnyRole("PACIENTE", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.GET, "/api/citas").hasAnyRole("MEDICO", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.GET, "/api/citas/citas-medico/**")
                            .hasAnyRole("MEDICO", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.PUT, "/api/citas/confirmar/**").hasRole("MEDICO")
                            .requestMatchers(HttpMethod.PUT, "/api/citas/atender/**").hasRole("MEDICO")
                            .requestMatchers(HttpMethod.PUT, "/api/citas/reprogramar/**")
                            .hasAnyRole("MEDICO", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.GET, "/api/citas/medico/**/pacientes")
                            .hasAnyRole("MEDICO", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.GET, "/api/citas/paciente/**/citas-futuras").hasRole("PACIENTE")
                            .requestMatchers(HttpMethod.PUT, "/api/citas/cancelar/**").hasRole("PACIENTE")
                            .requestMatchers(HttpMethod.GET, "/api/citas/{id}")
                            .hasAnyRole("PACIENTE", "MEDICO", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.PUT, "/api/citas/{id}")
                            .hasAnyRole("PACIENTE", "MEDICO", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.DELETE, "/api/citas/{id}")
                            .hasAnyRole("PACIENTE", "MEDICO", "RECEPCIONISTA")

                            .requestMatchers("/api/coberturas/**").hasRole("ADMINISTRADOR")

                            .requestMatchers("/api/disponibilidad/**")
                            .hasAnyRole("ADMINISTRADOR", "MEDICO", "RECEPCIONISTA")

                            .requestMatchers("/api/especialidades/**").hasRole("ADMINISTRADOR")

                            .requestMatchers("/api/horario-bloques/**")
                            .hasAnyRole("ADMINISTRADOR", "MEDICO", "RECEPCIONISTA")

                            .requestMatchers(HttpMethod.GET, "/api/medicos")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.GET, "/api/medicos/public")
                            .hasAnyRole("ADMINISTRADOR", "MEDICO", "PACIENTE", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.GET, "/api/medicos/{id}")
                            .hasAnyRole("ADMINISTRADOR", "MEDICO", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.GET, "/api/medicos/my-info/{id}").hasRole("MEDICO")
                            .requestMatchers(HttpMethod.POST, "/api/medicos").hasRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.PUT, "/api/medicos/{id}").hasAnyRole("ADMINISTRADOR", "MEDICO")
                            .requestMatchers(HttpMethod.DELETE, "/api/medicos/{id}").hasRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.GET, "/api/medicos/by-user/{usuarioId}")
                            .hasAnyRole("ADMINISTRADOR", "MEDICO")

                            .requestMatchers(HttpMethod.POST, "/api/medico-especialidad").hasRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.GET, "/api/medico-especialidad").hasRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.PUT, "/api/medico-especialidad/**").hasRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.DELETE, "/api/medico-especialidad/**").hasRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.GET, "/api/medico-especialidad/medico/**")
                            .hasAnyRole("ADMINISTRADOR", "MEDICO", "PACIENTE", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.GET, "/api/medico-especialidad/especialidad/**")
                            .hasAnyRole("ADMINISTRADOR", "MEDICO", "PACIENTE", "RECEPCIONISTA")

                            .requestMatchers(HttpMethod.GET, "/api/paciente-alergia")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.GET, "/api/paciente-alergia/paciente/**")
                            .hasAnyRole("ADMINISTRADOR", "MEDICO", "PACIENTE", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.GET, "/api/paciente-alergia/{id}")
                            .hasAnyRole("ADMINISTRADOR", "MEDICO", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.POST, "/api/paciente-alergia")
                            .hasAnyRole("ADMINISTRADOR", "MEDICO", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.PUT, "/api/paciente-alergia/**")
                            .hasAnyRole("ADMINISTRADOR", "MEDICO", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.DELETE, "/api/paciente-alergia/**")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")

                            .requestMatchers(HttpMethod.GET, "/api/pacientes")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.GET, "/api/pacientes/estado").hasRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.GET, "/api/pacientes/id/**")
                            .hasAnyRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.GET, "/api/pacientes/num-identificacion/**")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.GET, "/api/pacientes/my-info/**").hasRole("PACIENTE")
                            .requestMatchers(HttpMethod.POST, "/api/pacientes/datosIniciales").hasRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.PUT, "/api/pacientes/**")
                            .hasAnyRole("ADMINISTRADOR", "PACIENTE")
                            .requestMatchers(HttpMethod.DELETE, "/api/pacientes/**").hasRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.GET, "/api/pacientes/paginado")
                            .hasAnyRole("ADMINISTRADOR", "MEDICO")
                            .requestMatchers(HttpMethod.GET, "/api/recepcionistas").hasAnyRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.POST, "/api/recepcionistas").hasAnyRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.GET, "/api/recepcionistas/{id}").hasAnyRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.GET, "/api/recepcionistas/my-info/{id}")
                            .hasAnyRole("RECEPCIONISTA")
                            .requestMatchers(HttpMethod.PUT, "/api/recepcionistas/{id}").hasAnyRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.DELETE, "/api/recepcionistas/{id}").hasAnyRole("ADMINISTRADOR")
                            .requestMatchers("api/roles/**").hasRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.GET, "/api/seguro-coberturas")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.GET, "/api/seguro-coberturas/seguro/**")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.GET, "/api/seguro-coberturas/cobertura/**")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.GET, "/api/seguro-coberturas/{id}")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.POST, "/api/seguro-coberturas").hasRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.DELETE, "/api/seguro-coberturas/{id}").hasRole("ADMINISTRADOR")
                            .requestMatchers("api/seguros/**").hasAnyRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.GET, "/api/servicios")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "PACIENTE")
                            .requestMatchers(HttpMethod.GET, "/api/servicios/especialidad/**")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "PACIENTE")
                            .requestMatchers(HttpMethod.GET, "/api/servicios/paginado")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "PACIENTE")
                            .requestMatchers(HttpMethod.POST, "/api/servicios").hasRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.PUT, "/api/servicios/**").hasRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.DELETE, "/api/servicios/**").hasRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.GET, "/api/servicios-seguros")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "PACIENTE")
                            .requestMatchers(HttpMethod.GET, "/api/servicios-seguros/servicio/**")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "PACIENTE")
                            .requestMatchers(HttpMethod.GET, "/api/servicios-seguros/seguro/**")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "PACIENTE")
                            .requestMatchers(HttpMethod.GET, "/api/servicios-seguros/cobertura/**")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "PACIENTE")
                            .requestMatchers(HttpMethod.GET, "/api/servicios-seguros/{id}")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.POST, "/api/servicios-seguros").hasRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.DELETE, "/api/servicios-seguros/**").hasRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.GET, "/api/tipos-documentos")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "PACIENTE")
                            .requestMatchers(HttpMethod.GET, "/api/tipos-documentos/{id}")
                            .hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")
                            .requestMatchers(HttpMethod.POST, "/api/tipos-documentos").hasRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.PUT, "/api/tipos-documentos/{id}").hasRole("ADMINISTRADOR")
                            .requestMatchers(HttpMethod.DELETE, "/api/tipos-documentos/{id}").hasRole("ADMINISTRADOR")
                            .requestMatchers("api/usuarios/**").hasRole("ADMINISTRADOR")

                            .anyRequest().authenticated();

                })
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json"); // corregido: era aplication/json
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
