package org.bma.vento.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShortStatusResponseTest {

    private static final byte[] OFF_RESPONSE = new byte[] {0x6D, 0x61, 0x73, 0x74, 0x65, 0x72, 0x3, 0x0, 0x9, 0x0};
    private static final byte[] ON_RESPONSE = new byte[] {0x6D, 0x61, 0x73, 0x74, 0x65, 0x72, 0x3, 0x1, 0x9, 0x1};

    @Test
    public void turnedOffResponseParsedCorrectly() {
        ShortStatusResponse statusResponse = new ShortStatusResponse(OFF_RESPONSE);

        assertFalse(statusResponse.isTurnedOn());
    }

    @Test
    public void turnedOnResponseParsedCorrectly() {
        ShortStatusResponse statusResponse = new ShortStatusResponse(ON_RESPONSE);

        assertTrue(statusResponse.isTurnedOn());
    }

}