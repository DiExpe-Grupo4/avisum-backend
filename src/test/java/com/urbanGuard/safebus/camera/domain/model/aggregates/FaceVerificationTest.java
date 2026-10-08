package com.urbanGuard.safebus.camera.domain.model.aggregates;

import com.urbanGuard.safebus.camera.domain.model.commands.VerifyFaceCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Core Entity: FaceVerification")
class FaceVerificationTest {

    private final VerifyFaceCommand command = new VerifyFaceCommand(1L, "capture-001.jpg");

    @Test
    @DisplayName("Se crea con el comando, el resultado y el puntaje de confianza")
    void shouldCreateFromCommandAndResult() {
        var fv = new FaceVerification(command, "MATCH", 0.95);

        assertEquals(1L, fv.getEmployeeId());
        assertEquals("capture-001.jpg", fv.getCapturedImageRef());
        assertEquals("MATCH", fv.getMatchResult());
        assertEquals(0.95, fv.getConfidenceScore());
    }

    @Test
    @DisplayName("isMatch es true cuando el resultado es MATCH")
    void isMatchShouldBeTrueForMatch() {
        assertTrue(new FaceVerification(command, "MATCH", 0.95).isMatch());
    }

    @Test
    @DisplayName("isMatch es false cuando el resultado es NO_MATCH")
    void isMatchShouldBeFalseForNoMatch() {
        assertFalse(new FaceVerification(command, "NO_MATCH", 0.20).isMatch());
    }
}
