package org.bma.vento.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TurnOnOffRequestTest {

    private TurnOnOffRequest request;

    private static final byte[] ONOFF_REQUEST = new byte[] {0x6D, 0x6F, 0x62, 0x69, 0x6C, 0x65, 0x3, 0x0, 0x0D, 0x0A};

    @BeforeEach
    public void setup() {
        request = new TurnOnOffRequest();
    }

    @Test
    public void serializeTurnOnOffRequest() {
        assertArrayEquals(ONOFF_REQUEST, request.serialize());
    }

}