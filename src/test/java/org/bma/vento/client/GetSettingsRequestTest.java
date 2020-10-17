package org.bma.vento.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GetSettingsRequestTest {

    private GetSettingsRequest request;

    private static final byte[] GET_SETTINGS_REQUEST = new byte[] {0x6D, 0x6F, 0x62, 0x69, 0x6C, 0x65, 0x1, 0x0, 0x0D, 0x0A};

    @BeforeEach
    public void setup() {
        request = new GetSettingsRequest();
    }

    @Test
    public void serializeGetSettingsRequest() {
        assertArrayEquals(GET_SETTINGS_REQUEST, request.serialize());
    }

}