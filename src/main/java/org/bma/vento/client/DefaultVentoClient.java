package org.bma.vento.client;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Default implementation.
 */
@Slf4j
public class DefaultVentoClient implements VentoClient {

    private static final int RESPONSE_PACKET_SIZE = 40;
    private static final int SOCKET_TIMEOUT_MSEC = 30 * 1000;

    private final Supplier<DatagramSocket> socketSupplier;

    public DefaultVentoClient(Supplier<DatagramSocket> socketSupplier) {
        this.socketSupplier = socketSupplier;
    }

    public DefaultVentoClient() {
        this(DEFAULT_SOCKET);
    }

    public <T extends ClientResponse> T sendCommand(String host, int port, ClientRequest<T> request) throws VentoClientException  {
        try (DatagramSocket socket = socketSupplier.get()) {
            InetAddress address = InetAddress.getByName(host);
            sendRequest(socket, address, port, request);
            return receiveResponse(socket, address, port, request);
        } catch (IOException e) {
            VentoClientException ex = new VentoClientException("Error during sending command to " + host + ":" + port, e);
            if (e instanceof SocketTimeoutException) ex.setTimeout(true);
            throw ex;
        }
    }

    private <T extends ClientResponse> T receiveResponse(DatagramSocket socket, InetAddress address, int port, ClientRequest<T> request) throws IOException {
        byte[] buf = new byte[RESPONSE_PACKET_SIZE];

        long startReceivingTimeMs = System.currentTimeMillis();

        socket.receive(new DatagramPacket(buf, buf.length, address, port));

        log.debug("Received response: {} from host: {}, took time (ms): {}, body: {}",
                request.getClass().getSimpleName(),
                address.getHostName(),
                System.currentTimeMillis() - startReceivingTimeMs,
                Arrays.toString(buf));

        return request.createResponse(buf);
    }

    private void sendRequest(DatagramSocket socket, InetAddress address, int port, ClientRequest<?> request) throws IOException {
        byte[] buf = request.serialize();

        long startSendingTimeMs = System.currentTimeMillis();

        socket.send(new DatagramPacket(buf, buf.length, address, port));

        log.debug("Sent request: {} to host: {}, took time (ms): {}, body: {}",
                request.getClass().getSimpleName(),
                address.getHostName(),
                System.currentTimeMillis() - startSendingTimeMs,
                Arrays.toString(buf));
    }

    private static final Supplier<DatagramSocket> DEFAULT_SOCKET = () -> {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(SOCKET_TIMEOUT_MSEC);
            return socket;
        } catch (SocketException e) {
            throw new IllegalStateException("Cannot create connection socket", e);
        }
    };
}
