package com.clinicaregional.clinica.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API - Clínica Regional",
                description = "Documentación de la API para el backend de Clínica Regional",
                version = "1.0.0",
                termsOfService = "https://clinicaregional.pe/terminos",
                contact = @Contact(
                        name = "Diego Aguilar",
                        url = "https://clinicaregional.pe",
                        email = "diegoaguilar5461@gmail.com"
                ),
                license = @License(
                        name = "Licencia de uso interno",
                        url = "https://clinicaregional.pe/licencia"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Servidor Local"),
                @Server(url = "https://clinicaregional.pe/api", description = "Servidor Producción")
        },
        security = {
                @SecurityRequirement(name = "Security Token")
        }
)
@SecurityScheme(
        name = "Security Token",
        description = "JWT token necesario para autenticación",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        paramName = HttpHeaders.AUTHORIZATION
)
public class SwaggerConfig {
}
