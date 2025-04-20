package com.clinicaregional.clinica;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class ClinicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClinicaApplication.class, args);
	}


	@Autowired
private PasswordEncoder passwordEncoder;

@PostConstruct
public void init() {
    String hashed = passwordEncoder.encode("admin123");
    System.out.println("Hash correcto para 'admin123': " + hashed);
	String hashed2 = passwordEncoder.encode("recepcionista1234");
	System.out.println("Hash correcto para 'recepcionista1234': " + hashed2);
	String hashed3 = passwordEncoder.encode("medico1234");
	System.out.println("Hash correcto para 'medico1234': " + hashed3);
	String hashed4 = passwordEncoder.encode("paciente1234");
	System.out.println("Hash correcto para 'paciente1234': " + hashed4);
}

}
