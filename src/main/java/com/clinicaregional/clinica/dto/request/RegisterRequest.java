package com.clinicaregional.clinica.dto.request;

import com.clinicaregional.clinica.dto.PacienteConUserDTO;
import com.clinicaregional.clinica.dto.PacienteSinUserDTO;
import com.clinicaregional.clinica.enums.ModalidadDeAtencion;
import com.clinicaregional.clinica.enums.Sexo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    // Datos de usuario
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 32)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).*$")
    private String password;

    // Datos personales
    @NotBlank
    @Size(min = 2, max = 48)
    private String nombres;

    @NotBlank
    @Size(min = 2, max = 64)
    private String apellidos;

    @NotNull
    @PastOrPresent
    private LocalDate fechaNacimiento;

    @NotNull
    private Sexo sexo;

    // Documento
    @NotNull
    private Long tipoDocumentoId; // Solo necesitamos el ID

    @NotBlank
    private String numeroDocumento;

    // Contacto
    @NotBlank
    private String telefono;

    @NotBlank
    private String direccion;

    // Seguro (opcional)
    private Long seguroId; // Solo ID si tiene seguro
    private String numeroPoliza;

    @NotNull
    private ModalidadDeAtencion modalidadAtencion;

    // Datos adicionales (opcionales)
    private String contactoEmergenciaNombre;
    private String contactoEmergenciaTelefono;
}