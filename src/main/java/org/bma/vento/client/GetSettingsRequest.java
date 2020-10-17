package org.bma.vento.client;

/**
 * Getting current settings from a remote host.
 */
public class GetSettingsRequest extends AbstractClientRequest<ShortStatusResponse> {

    private static final byte[] REQUEST = new byte[] {0x1, 0x0};

    @Override
    protected byte[] serializeCommand() {
        return REQUEST;
    }

    @Override
    public ShortStatusResponse createResponse(byte[] response) {
        return new ShortStatusResponse(response);
    }
}
