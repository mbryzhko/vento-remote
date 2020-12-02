package org.bma.vento.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.function.Supplier;

@Slf4j
public class VentoClient {

    private static final int RESPONSE_PACKET_SIZE = 40;

    private final Supplier<DatagramSocket> socketSupplier;

    public VentoClient(Supplier<DatagramSocket> socketSupplier) {
        this.socketSupplier = socketSupplier;
    }

    public VentoClient() {
        this(DEFAULT_SOCKET);
    }

    public <T extends ClientResponse> T sendCommand(String host, int port, ClientRequest<T> request)  {
        try (DatagramSocket socket = socketSupplier.get()) {
            InetAddress address = InetAddress.getByName(host);
            sendRequest(socket, address, port, request);
            return receiveResponse(socket, request);
        } catch (IOException e) {
            throw new VentoClientException("Error during sending command to " + host + ":" + port, e);
        }
    }

    private <T extends ClientResponse> T receiveResponse(DatagramSocket socket, ClientRequest<T> request) throws IOException {
        byte[] buf = new byte[RESPONSE_PACKET_SIZE];
        socket.receive(new DatagramPacket(buf, buf.length));

        log.debug("Received from: {}, response: {}", socket.getInetAddress(), Arrays.toString(buf));

        return request.createResponse(buf);
    }

    private void sendRequest(DatagramSocket socket, InetAddress address, int port, ClientRequest<?> request) throws IOException {
        byte[] buf = request.serialize();

        log.debug("Sending to: {}, command: {} ", address, Arrays.toString(buf));

        socket.send(new DatagramPacket(buf, buf.length, address, port));
    }

    private static final Supplier<DatagramSocket> DEFAULT_SOCKET = () -> {
        try {
            return new DatagramSocket();
        } catch (SocketException e) {
            throw new IllegalStateException("Cannot create connection socket", e);
        }
    };
}
