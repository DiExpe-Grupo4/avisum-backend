package com.urbanGuard.safebus.bdd.support;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicInteger;

/** Cliente HTTP mínimo para llamar a la API REST real. */
public final class Api {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final AtomicInteger SEQ = new AtomicInteger(5000);

    private Api() {}

    public static int next() { return SEQ.incrementAndGet(); }
    public static String uniqueDni() { return String.format("%08d", 30_000_000 + next()); }
    public static String uniqueEmail() { return "bdd" + next() + "@test.safebus.com"; }
    public static String uniquePlate() { return "BDD-" + next(); }

    public static HttpResponse<String> get(String path) {
        return send(HttpRequest.newBuilder(uri(path)).GET());
    }

    public static HttpResponse<String> post(String path, String json) {
        return send(HttpRequest.newBuilder(uri(path)).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)));
    }

    public static HttpResponse<String> put(String path, String json) {
        return send(HttpRequest.newBuilder(uri(path)).header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json)));
    }

    public static HttpResponse<String> patch(String path, String json) {
        return send(HttpRequest.newBuilder(uri(path)).header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json)));
    }

    private static HttpResponse<String> send(HttpRequest.Builder builder) {
        try {
            return CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new IllegalStateException("Error llamando a la API: " + e.getMessage(), e);
        }
    }

    private static URI uri(String path) { return URI.create(TestServer.baseUrl() + path); }
}
