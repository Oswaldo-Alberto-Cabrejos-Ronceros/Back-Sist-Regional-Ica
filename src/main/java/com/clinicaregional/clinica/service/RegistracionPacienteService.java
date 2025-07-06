package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.authentication.CompletePatientRegistrationRequest;
import com.clinicaregional.clinica.dto.authentication.VerifyCodeRequest;
import com.clinicaregional.clinica.dto.authentication.VerifyEmailRequest;
import com.clinicaregional.clinica.dto.authentication.VerifyPatientRequest;
import com.clinicaregional.clinica.dto.request.RegisterRequest;
import com.clinicaregional.clinica.dto.response.AuthenticationResponseDTO;

public interface RegistracionPacienteService {
    /**
     * Verifica si un paciente ya está registrado en el sistema
     */
    boolean checkPatientExists(VerifyPatientRequest request);

    /**
     * Envía un código de verificación al email del paciente
     */
    void sendVerificationEmail(VerifyEmailRequest request);

    /**
     * Verifica si el código ingresado coincide con el enviado al email
     */
    boolean verifyCode(VerifyCodeRequest request, String email);

    /**
     * Completa el registro de un paciente que ya existía en el sistema
     */
    AuthenticationResponseDTO completeExistingPatientRegistration(
            String documento,
            CompletePatientRegistrationRequest request);

    /**
     * Registra un nuevo paciente en el sistema
     */
    AuthenticationResponseDTO registerNewPatient(RegisterRequest request);
}
