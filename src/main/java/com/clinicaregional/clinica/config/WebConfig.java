package com.clinicaregional.clinica.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry){
        //activamos cors para todos los endpoints
        corsRegistry.addMapping("/**")
                .allowedOrigins("http://localhost:5173","https://localhost:5173") //origines permitidos
                .allowedMethods("GET","POST","PUT","DELETE") //metodos permitidos
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
