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
        return String.format(
                """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <style>
                                body {
                                    font-family: 'Arial', sans-serif;
                                    background-color: #f5f7fa;
                                    margin: 0;
                                    padding: 0;
                                    color: #ffffff; /* Texto blanco por defecto */
                                }
                                .container {
                                    max-width: 600px;
                                    margin: 20px auto;
                                    background: #ffffff;
                                    border-radius: 8px;
                                    overflow: hidden;
                                    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                                }
                                .header {
                                    background: #001f3f; /* Azul marino */
                                    padding: 30px 20px;
                                    text-align: center;
                                }
                                .header h2 {
                                    margin: 0;
                                    font-size: 24px;
                                    font-weight: 600;
                                }
                                .content {
                                    padding: 30px;
                                    color: #333333; /* Texto oscuro para contenido */
                                }
                                .credentials {
                                    background: #f8f9fa;
                                    padding: 20px;
                                    border-radius: 6px;
                                    margin: 25px 0;
                                    border-left: 4px solid #0ea5e9;
                                    color: #333333;
                                }
                                .credentials h3 {
                                    color: #001f3f;
                                    margin-top: 0;
                                }
                                .button {
                                    display: inline-block;
                                    padding: 12px 24px;
                                    background: #0ea5e9;
                                    color: white !important;
                                    text-decoration: none;
                                    border-radius: 25px;
                                    font-weight: bold;
                                    margin: 15px 0;
                                }
                                .footer {
                                    text-align: center;
                                    padding: 20px;
                                    font-size: 12px;
                                    color: #9ca3af;
                                    background: #f8f9fa;
                                }
                                .highlight-text {
                                    color: #001f3f;
                                    font-weight: bold;
                                }
                            </style>
                        </head>
                        <body>
                            <div class="container">
                                <div class="header">
                                    <h2>¡Registro Completado Exitosamente!</h2>
                                </div>
                                <div class="content">
                                    <p>Estimado/a <span class="highlight-text">%s %s</span>,</p>

                                    <p>Su cuenta ha sido creada exitosamente en el sistema de <strong>Clínica Regional Ica</strong>.</p>

                                    <div class="credentials">
                                        <h3>Sus credenciales de acceso:</h3>
                                        <p><strong>Usuario:</strong> %s</p>
                                        <p><strong>Contraseña:</strong> La que ingresó durante el registro</p>
                                        <p style="font-size: 13px; margin-top: 15px;">
                                            <em>Por seguridad, recomendamos cambiar su contraseña después del primer acceso.</em>
                                        </p>
                                    </div>

                                    <div style="text-align: center;">
                                        <a href="https://clinica.com/login" class="button">Acceder al Sistema</a>
                                    </div>

                                    <p>Si no reconoce esta actividad, por favor contacte a nuestro equipo de soporte inmediatamente.</p>
                                </div>
                                <div class="footer">
                                    <p>© %d Clínica Regional Ica. Todos los derechos reservados.</p>
                                    <p>Av. San Martín 123, Ica, Perú | Teléfono: (056) 123456</p>
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