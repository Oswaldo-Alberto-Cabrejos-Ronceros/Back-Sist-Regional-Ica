package com.clinicaregional.clinica.config;

import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateFilterConfig {

    @Bean
    public FiltroEstado filtroEstado() {
        return new FiltroEstado() {}; // clase anónima porque FiltroEstado es abstract
    }
}

