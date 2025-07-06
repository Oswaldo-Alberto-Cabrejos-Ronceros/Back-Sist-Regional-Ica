package com.clinicaregional.clinica.service.impl.email;

import com.clinicaregional.clinica.dto.PacienteSinUserDTO;
import com.clinicaregional.clinica.enums.ModalidadDeAtencion;
import com.clinicaregional.clinica.exception.EmailSendingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.Period;

@Service
@Slf4j
@RequiredArgsConstructor
public class BienvenidaEmailService {

    private final JavaMailSender mailSender;

    public void enviarEmailBienvenida(PacienteSinUserDTO paciente) {
        String contenidoHtml = construirHtmlBienvenida(paciente);
        enviarCorreo(
                paciente.getEmail(),
                "Bienvenido a Clínica Regional Ica",
                contenidoHtml);
    }

    private String construirHtmlBienvenida(PacienteSinUserDTO p) {
        if (p == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }

        int edad = calcularEdad(p.getFechaNacimiento());
        String modalidad = p.getModalidadDeAtencion() == ModalidadDeAtencion.SEGURO ? "Seguro" : "Particular";

        return String.format(
                """
                        <!DOCTYPE html>
                        <html lang="es">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Bienvenido a Clínica Regional Ica</title>
                            <style>
                                body { font-family: Arial, sans-serif; background-color: #f3f4f6; color: #333; margin: 0; padding: 0; }
                                .container { max-width: 600px; margin: auto; background: #ffffff; border-radius: 8px; overflow: hidden; }
                                .header { background-color: #001f3f; /* Azul marino */ padding: 20px; color: white; }
                                .logo { border-radius: 50%%; border: 2px solid white; width: 60px; height: 60px; }
                                .data-table { width: 100%%; font-size: 15px; background-color: #f9fafb; border-radius: 6px; border: 1px solid #e5e7eb; }
                                .btn-primary { display: inline-block; padding: 12px 20px; background-color: #0ea5e9; color: white;
                                             text-decoration: none; border-radius: 25px; font-weight: bold; }
                                .footer { text-align: center; font-size: 12px; color: #9ca3af; background: #f9fafb; padding: 15px; }
                            </style>
                        </head>
                        <body>
                            <div class="container">
                                <div class="header">
                                    <table width="100%%" style="border-collapse: collapse;">
                                        <tr>
                                            <td style="width: 80px;">
                                                <img src="https://scontent.flim3-2.fna.fbcdn.net/v/t39.30808-6/454614062_122100882722461099_649067528575904421_n.jpg?_nc_cat=108&ccb=1-7&_nc_sid=6ee11a&_nc_eui2=AeG7SlzeH5mzgCRP7lHaydPnE0JRzhhy7aATQlHOGHLtoFcQl7Trzi3yn--jiRTSNDiHm7VQN4XpNglPkmNQaVKb&_nc_ohc=F1VJMcvA0_QQ7kNvwENx8vk&_nc_oc=AdnXKGaVpHee6VyDAFrzr4vZOkHptcwVfTyVb6K-D4maBl0Bugo9R7tEbnLRr8LNQlE&_nc_zt=23&_nc_ht=scontent.flim3-2.fna&_nc_gid=TdCnCgKJx14p-NpRvxRKwg&oh=00_AfS-lgE7OCrfFftOB2pAhWmaZraCBiuxY5vCqUaeAojOrQ&oe=686FA455"
                                                     alt="Logo Clínica" class="logo">
                                            </td>
                                            <td style="text-align: center; color: white;"> <!-- Texto en blanco -->
                                                <h1 style="margin: 0; font-size: 22px; font-weight: 600; color: white;">Bienvenido a Clínica Regional Ica</h1>
                                            </td>
                                        </tr>
                                    </table>
                                </div>

                                <div style="padding: 20px;">
                                    <p style="font-size: 16px;">Estimado/a <strong>%s %s</strong>,</p>
                                    <p style="font-size: 16px;">En nombre de todo el equipo de <strong>Clínica Regional Ica</strong>,
                                    nos complace darle la más cordial bienvenida.</p>

                                    <h3 style="color: #1e40af;">Sus datos de registro:</h3>
                                    <table class="data-table" cellpadding="10">
                                        <tr><td style="font-weight: bold; width: 40%%;">Documento:</td><td>%s</td></tr>
                                        <tr><td style="font-weight: bold;">Nombre completo:</td><td>%s %s</td></tr>
                                        <tr><td style="font-weight: bold;">Fecha de nacimiento:</td><td>%s (%d años)</td></tr>
                                        <tr><td style="font-weight: bold;">Sexo:</td><td>%s</td></tr>
                                        <tr><td style="font-weight: bold;">Nacionalidad:</td><td>%s</td></tr>
                                        <tr><td style="font-weight: bold;">Teléfono:</td><td>%s</td></tr>
                                        <tr><td style="font-weight: bold;">Dirección:</td><td>%s</td></tr>
                                        <tr><td style="font-weight: bold;">Email:</td><td>%s</td></tr>
                                        <tr><td style="font-weight: bold;">Modalidad de atención:</td><td>%s</td></tr>
                                    </table>

                                    <div style="text-align: center; margin: 25px 0;">
                                        <a href="https://clinica-regional-ica-git-develop-alyri03s-projects.vercel.app/" class="btn-primary">Agendar mi primera cita</a>
                                    </div>

                                    <p style="font-style: italic;">Atentamente,<br>
                                    <strong>Dr. Carlos Enrique Navea Méndez/strong><br>
                                    Director Médico<br>
                                    Clínica Regional Ica</p>
                                </div>

                                <div class="footer">
                                    © 2023 Clínica Regional Ica. Todos los derechos reservados.<br>
                                    Av. San Martín 123, Ica, Perú | Teléfono: (056) 123456
                                </div>
                            </div>
                        </body>
                        </html>
                        """,
                p.getNombres(),
                p.getApellidos(),
                p.getNumeroIdentificacion(),
                p.getNombres(),
                p.getApellidos(),
                p.getFechaNacimiento().toString(),
                edad,
                p.getSexo().toString(),
                p.getNacionalidad(),
                p.getTelefono(),
                p.getDireccion(),
                p.getEmail(),
                modalidad);
    }

    private void enviarCorreo(String para, String asunto, String contenidoHtml) {
        if (para == null || para.isBlank()) {
            throw new IllegalArgumentException("El destinatario no puede estar vacío");
        }
        if (asunto == null || asunto.isBlank()) {
            throw new IllegalArgumentException("El asunto no puede estar vacío");
        }

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(para);
            helper.setSubject(asunto);
            helper.setText(contenidoHtml, true);
            helper.setPriority(1);

            mailSender.send(mensaje);
            log.info("Correo de bienvenida enviado exitosamente a: {}", para);
        } catch (Exception e) {
            log.error("Error al enviar correo a {}: {}", para, e.getMessage(), e);
            throw new EmailSendingException("Error al enviar correo electrónico", e);
        }
    }

    private int calcularEdad(LocalDate fechaNacimiento) {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}