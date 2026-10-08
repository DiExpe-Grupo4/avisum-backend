package com.urbanGuard.safebus.bdd.steps;

import com.urbanGuard.safebus.bdd.support.TestServer;
import com.urbanGuard.safebus.bdd.support.World;
import io.cucumber.java.Before;

public class Hooks {

    @Before
    public void antesDeCadaEscenario() {
        TestServer.baseUrl(); // arranca la aplicación la primera vez
        World.reset();
    }
}
