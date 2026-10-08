package com.urbanGuard.safebus.bdd.support;

import com.urbanGuard.safebus.SafeBusApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.ServerSocket;

/**
 * Levanta UNA sola vez la aplicación real (puerto libre + H2 en memoria propia)
 * y la comparte entre todos los escenarios.
 */
public final class TestServer {

    private static ConfigurableApplicationContext context;
    private static int port;

    private TestServer() {}

    public static synchronized String baseUrl() {
        if (context == null) start();
        return "http://localhost:" + port;
    }

    private static void start() {
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo reservar un puerto libre", e);
        }
        context = SpringApplication.run(SafeBusApplication.class,
                "--server.port=" + port,
                "--spring.main.banner-mode=off",
                "--spring.datasource.url=jdbc:h2:mem:safebus_bdd;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (context != null) context.close();
        }));
    }
}
