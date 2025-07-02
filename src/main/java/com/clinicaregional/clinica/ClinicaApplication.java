package com.clinicaregional.clinica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClinicaApplication {
	public static void main(String[] args) {
		SpringApplication.run(ClinicaApplication.class, args);
	}

}
