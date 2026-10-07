package com.urbanGuard.safebus.shared.infrastructure.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String fullName, String employeeCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject("Bienvenido a Avisum");
            message.setText(
                    "Hola " + fullName + ",\n\n" +
                    "Bienvenido a Avisum.\n" +
                    "Tu código de acceso es: " + employeeCode + "\n\n" +
                    "Ingresa a la plataforma con este código para iniciar tu turno.\n\n" +
                    "Equipo Avisum"
            );
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("No se pudo enviar el correo de bienvenida: " + e.getMessage());
        }
    }
}
