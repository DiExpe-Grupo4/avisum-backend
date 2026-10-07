package com.urbanGuard.safebus.shared.infrastructure.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${brevo.api.key:}")
    private String apiKey;

    @Value("${brevo.sender.email:avisumcorp@gmail.com}")
    private String senderEmail;

    @Value("${brevo.sender.name:Avisum}")
    private String senderName;

    @Async
    public void sendWelcomeEmail(String toEmail, String fullName, String employeeCode) {
        try {
            Map<String, Object> body = Map.of(
                    "sender", Map.of("name", senderName, "email", senderEmail),
                    "to", List.of(Map.of("email", toEmail, "name", fullName)),
                    "subject", "Bienvenido a Avisum",
                    "textContent",
                    "Hola " + fullName + ",\n\n" +
                    "Bienvenido a Avisum.\n" +
                    "Tu código de acceso es: " + employeeCode + "\n\n" +
                    "Ingresa a la plataforma con este código para iniciar tu turno.\n\n" +
                    "Equipo Avisum"
            );

            String json = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("accept", "application/json")
                    .header("api-key", apiKey)
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                System.err.println("No se pudo enviar el correo de bienvenida: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            System.err.println("No se pudo enviar el correo de bienvenida: " + e.getMessage());
        }
    }
}
