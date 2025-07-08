package com.clinicaregional.clinica.service;

public interface EmailVerificacionService {
    /**
     * Genera y envía un código de verificación al email especificado
     */
    void sendVerificationCode(String email);

    /**
     * Verifica si el código proporcionado coincide con el enviado al email
     */
    boolean verifyCode(String email, String code);

    /**
     * Limpia los códigos expirados del almacenamiento
     */
    void cleanUpExpiredCodes();

}
