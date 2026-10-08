package com.urbanGuard.safebus.bdd.support;

import java.net.http.HttpResponse;

/** Estado compartido entre los pasos de un mismo escenario (se reinicia en Hooks). */
public final class World {

    public static HttpResponse<String> response;
    public static long employeeId;
    public static String employeeCode;
    public static String password;
    public static long busId;
    public static String plate;
    public static long shiftId;
    public static long alertId;
    public static int countBefore;

    private World() {}

    public static void reset() {
        response = null;
        employeeId = 0;
        employeeCode = null;
        password = null;
        busId = 0;
        plate = null;
        shiftId = 0;
        alertId = 0;
        countBefore = 0;
    }
}
