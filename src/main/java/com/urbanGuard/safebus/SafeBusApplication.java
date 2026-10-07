package com.urbanGuard.safebus;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@OpenAPIDefinition(servers = @Server(url = "/", description = "Servidor actual"))
public class SafeBusApplication {
    public static void main(String[] args) {
        SpringApplication.run(SafeBusApplication.class, args);
    }
}
