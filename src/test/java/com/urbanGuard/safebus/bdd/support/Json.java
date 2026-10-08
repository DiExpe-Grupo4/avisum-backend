package com.urbanGuard.safebus.bdd.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Utilidades para leer campos de las respuestas JSON sin depender de una librería. */
public final class Json {

    private Json() {}

    public static long longField(String json, String name) {
        Matcher m = Pattern.compile("\"" + name + "\"\\s*:\\s*(\\d+)").matcher(json);
        if (!m.find()) throw new AssertionError("No se encontró '" + name + "' en: " + json);
        return Long.parseLong(m.group(1));
    }

    public static String stringField(String json, String name) {
        Matcher m = Pattern.compile("\"" + name + "\"\\s*:\\s*\"([^\"]*)\"").matcher(json);
        if (!m.find()) throw new AssertionError("No se encontró '" + name + "' en: " + json);
        return m.group(1);
    }

    /** Cuántas veces aparece un texto (sirve para contar elementos de una lista JSON). */
    public static int count(String json, String token) {
        int count = 0, from = 0;
        while ((from = json.indexOf(token, from)) >= 0) { count++; from += token.length(); }
        return count;
    }

    /** true si el campo existe y tiene un valor numérico (no null). */
    public static boolean hasNumber(String json, String field) {
        return Pattern.compile("\"" + field + "\"\\s*:\\s*-?\\d").matcher(json).find();
    }

    /** true si el campo existe y su valor no es null. */
    public static boolean hasValue(String json, String field) {
        return Pattern.compile("\"" + field + "\"\\s*:\\s*[^n\\s,}]").matcher(json).find();
    }
}
