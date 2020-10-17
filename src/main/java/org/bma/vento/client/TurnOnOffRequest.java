package org.bma.vento.client;

/**
 * Turning on or off the ventilation.
 */
public class TurnOnOffRequest extends AbstractClientRequest<ShortStatusResponse> {

    private static final byte[] REQUEST = new byte[] {0x3, 0x0};

    @Override
    protected byte[] serializeCommand() {
        return REQUEST;
    }

    @Override
    public ShortStatusResponse createResponse(byte[] response) {
        return new ShortStatusResponse(response);
    }

}
