package org.bma.vento.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class SetSpeedRequestTest {

    private static final byte[] PREFIX = new byte[] {0x6D, 0x6F, 0x62, 0x69, 0x6C, 0x65};
    private static final byte[] TERM = new byte[] {0x0D, 0x0A};

    @Test
    public void serializeSetSpeedRequestWithSpeed1() {
        SetSpeedRequest request = new SetSpeedRequest(1);
        byte[] expected = buildExpected((byte) 1);
        assertArrayEquals(expected, request.serialize());
    }

    @Test
    public void serializeSetSpeedRequestWithSpeed2() {
        SetSpeedRequest request = new SetSpeedRequest(2);
        byte[] expected = buildExpected((byte) 2);
        assertArrayEquals(expected, request.serialize());
    }

    @Test
    public void serializeSetSpeedRequestWithSpeed3() {
        SetSpeedRequest request = new SetSpeedRequest(3);
        byte[] expected = buildExpected((byte) 3);
        assertArrayEquals(expected, request.serialize());
    }

    private byte[] buildExpected(byte speed) {
        return new byte[] {0x6D, 0x6F, 0x62, 0x69, 0x6C, 0x65, 0x4, speed, 0x0D, 0x0A};
    }
}