package com.clinicaregional.clinica.service.impl;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.clinicaregional.clinica.service.EmailVerificacionService;

@Service
public class EmailVerificacionServiceImpl implements EmailVerificacionService {

    private static class CodeData {
        String code;
        long creationTime;

        CodeData(String code, long creationTime) {
            this.code = code;
            this.creationTime = creationTime;
        }
    }

    // Cambiado a Map<String, CodeData> en lugar de Map<String, String>
    private final Map<String, CodeData> verificationCodes = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private final JavaMailSender mailSender;
    private static final long EXPIRATION_MINUTES = 10;

    public EmailVerificacionServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationCode(String email) {
        // Validar formato de email
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email no válido");
        }
        
        // Generar código
        String code = String.format("%06d", random.nextInt(999999));

        // Guardar código con timestamp (ahora usando CodeData)
        verificationCodes.put(email, new CodeData(code, System.currentTimeMillis()));

        // Enviar email
        sendVerificationEmail(email, code);
    }

    @Override
    public boolean verifyCode(String email, String code) {
        // Validaciones básicas
        if (email == null || code == null || code.length() != 6) {
            return false;
        }

        CodeData codeData = verificationCodes.get(email);
        if (codeData == null) {
            return false;
        }

        // Verificar expiración (10 minutos)
        long elapsedTime = System.currentTimeMillis() - codeData.creationTime;
        boolean expired = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) > EXPIRATION_MINUTES;

        if (expired) {
            verificationCodes.remove(email);
            return false;
        }

        // Verificar código
        boolean isValid = code.equals(codeData.code);
        if (isValid) {
            verificationCodes.remove(email); // Eliminar código una vez verificado
        }

        return isValid;
    }

    @Override
    public void cleanUpExpiredCodes() {
        long currentTime = System.currentTimeMillis();
        verificationCodes.entrySet().removeIf(entry -> TimeUnit.MILLISECONDS
                .toMinutes(currentTime - entry.getValue().creationTime) > EXPIRATION_MINUTES);
    }

    private void sendVerificationEmail(String email, String code) {
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Código de Verificación - Clínica Regional");
        message.setText(String.format(
                "Su código de verificación es: %s\n\n" +
                        "Este código expirará en %d minutos. No lo comparta con nadie.",
                code, EXPIRATION_MINUTES));

        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar email de verificación", e);
        }
    }
}