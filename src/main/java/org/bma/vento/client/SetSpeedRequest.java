package org.bma.vento.client;

/**
 * Sets the fan speed of the ventilation unit.
 * Speed values: 1 (low), 2 (medium), 3 (high).
 */
public class SetSpeedRequest extends AbstractClientRequest<ShortStatusResponse> {

    private final byte speed;

    public SetSpeedRequest(int speed) {
        this.speed = (byte) speed;
    }

    @Override
    protected byte[] serializeCommand() {
        return new byte[] {0x4, speed};
    }

    @Override
    public ShortStatusResponse createResponse(byte[] response) {
        return new ShortStatusResponse(response);
    }
}