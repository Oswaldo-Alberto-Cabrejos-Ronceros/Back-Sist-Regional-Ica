package com.clinicaregional.clinica.service.impl.email;

import java.time.LocalDate;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.clinicaregional.clinica.dto.PacienteConUserDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.exception.EmailSendingException;
import com.clinicaregional.clinica.mapper.PacienteMapper;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistroCompletoEmailService {

    private final JavaMailSender mailSender;
    private final PacienteMapper pacienteMapper;

    public void enviarConfirmacionRegistro(PacienteConUserDTO paciente, UsuarioDTO usuario) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(paciente.getEmail());
            helper.setSubject("Confirmación de Registro - Clínica Regional Ica");
            helper.setText(construirEmailConfirmacion(paciente, usuario), true);
            helper.setPriority(1); // Añadido para prioridad alta

            mailSender.send(mensaje);
            log.info("Email de confirmación enviado a {}", paciente.getEmail());
        } catch (Exception e) {
            log.error("Error enviando email de confirmación para {}", paciente.getEmail(), e);
            throw new EmailSendingException("Error enviando email de confirmación", e);
        }
    }

    private String construirEmailConfirmacion(PacienteConUserDTO paciente, UsuarioDTO usuario) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: auto; border: 1px solid #ddd; border-radius: 5px; }
                        .header { background: #0369a1; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; }
                        .credentials {
                            background: #f3f4f6;
                            padding: 15px;
                            border-radius: 5px;
                            margin: 15px 0;
                            border-left: 4px solid #0ea5e9;
                        }
                        .button {
                            display: inline-block;
                            padding: 10px 20px;
                            background: #0ea5e9;
                            color: white;
                            text-decoration: none;
                            border-radius: 5px;
                        }
                        .footer { text-align: center; padding: 10px; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>¡Registro Completado Exitosamente!</h2>
                        </div>
                        <div class="content">
                            <p>Estimado/a <strong>%s %s</strong>,</p>

                            <p>Su cuenta ha sido creada exitosamente en nuestro sistema.</p>

                            <div class="credentials">
                                <h3>Sus credenciales de acceso:</h3>
                                <p><strong>Usuario:</strong> %s</p>
                                <p><strong>Contraseña:</strong> La que ingresó durante el registro</p>
                            </div>

                            <p style="text-align: center;">
                                <a href="https://clinica.com/login" class="button">Acceder al Sistema</a>
                            </p>

                            <p>Recomendamos cambiar su contraseña después del primer acceso.</p>
                        </div>
                        <div class="footer">
                            <p>© %d Clínica Regional Ica. Todos los derechos reservados.</p>
                            <p>Este es un mensaje automático, por favor no responda a este correo.</p>
                        </div>
                    </div>
                </body>
                </html>
                """,
                paciente.getNombres(),
                paciente.getApellidos(),
                usuario.getCorreo(),
                LocalDate.now().getYear());
    }
}