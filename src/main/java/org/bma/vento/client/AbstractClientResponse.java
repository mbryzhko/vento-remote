package org.bma.vento.client;

/**
 * Implements common logic of {@code ClientResponse}.
 */
public abstract class AbstractClientResponse implements ClientResponse {

    private static final int PREFIX_LEN = 6;

    public AbstractClientResponse(byte[] response) {

        byte[] cmd = new byte[response.length - PREFIX_LEN];

        System.arraycopy(response, PREFIX_LEN, cmd, 0, cmd.length);

        parseCommand(cmd);

    }

    protected abstract void parseCommand(byte[] cmd);
}
