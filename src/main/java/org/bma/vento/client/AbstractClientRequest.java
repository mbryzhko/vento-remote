package org.bma.vento.client;

/**
 * Implements common logic of {@code ClientRequest}.
 *
 * @param <R> type of response.
 * @see ClientResponse
 */
public abstract class AbstractClientRequest<R extends ClientResponse> implements ClientRequest<R> {

    private static byte[] CMD_PREFIX = new byte[] {0x6D, 0x6F, 0x62, 0x69, 0x6C, 0x65};
    private static byte[] CMD_TERM = new byte[] {0x0D, 0x0A};

    @Override
    public final byte[] serialize() {
        byte[] cmd = serializeCommand();
        byte[] buf = new byte[CMD_PREFIX.length + cmd.length + CMD_TERM.length];

        System.arraycopy(CMD_PREFIX, 0, buf, 0, CMD_PREFIX.length);
        System.arraycopy(cmd, 0, buf, CMD_PREFIX.length, cmd.length);
        System.arraycopy(CMD_TERM, 0, buf, CMD_PREFIX.length + cmd.length, CMD_TERM.length);

        return buf;
    }

    protected abstract byte[] serializeCommand();
}
