package org.bma.vento.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultVentoClientTest {

    private static final String HOST = "localhost";
    private static final int PORT = 4000;
    private static final byte[] SERIALIZED_REQUEST = new byte[] {1, 2, 3, 4};
    private static final byte[] RAW_RESPONSE = new byte[] {5, 6, 7, 8};

    private DefaultVentoClient client;

    @Mock
    private DatagramSocket socket;

    @Mock
    private ClientRequest<ClientResponse> request;
    @Mock
    private ClientResponse response;

    @BeforeEach
    public void setup() {
        client = new DefaultVentoClient(() -> socket);
    }

    @Test
    public void serializedRequestIsSent() throws IOException {
        Mockito.when(request.serialize()).thenReturn(SERIALIZED_REQUEST);
        Mockito.when(request.createResponse(Mockito.any())).thenReturn(response);

        client.sendCommand(HOST, PORT, request);

        ArgumentCaptor<DatagramPacket> datagramPacketCaptor = ArgumentCaptor.forClass(DatagramPacket.class);
        Mockito.verify(socket, Mockito.times(1)).send(datagramPacketCaptor.capture());
        DatagramPacket sentPacket = datagramPacketCaptor.getValue();

        assertEquals(HOST, sentPacket.getAddress().getHostName());
        assertEquals(PORT, sentPacket.getPort());
        assertArrayEquals(SERIALIZED_REQUEST, sentPacket.getData());
    }

    @Test
    public void sockedIsClosedAfterCommandIsSent() {
        Mockito.when(request.serialize()).thenReturn(SERIALIZED_REQUEST);
        Mockito.when(request.createResponse(Mockito.any())).thenReturn(response);

        client.sendCommand(HOST, PORT, request);

        Mockito.verify(socket, Mockito.times(1)).close();
    }

    @Test
    public void ventoClientExceptionWhenSomethingWentWrong()  {
        VentoClientException ex = assertThrows(VentoClientException.class, () -> {
            Mockito.when(request.serialize()).thenReturn(SERIALIZED_REQUEST);
            Mockito.doThrow(IOException.class).when(socket).send(Mockito.any());
            client.sendCommand(HOST, PORT, request);
        });

        assertFalse(ex.isTimeout());
    }

    @Test
    public void ventoClientTimeoutException()  {
        VentoClientException ex = assertThrows(VentoClientException.class, () -> {
            Mockito.when(request.serialize()).thenReturn(SERIALIZED_REQUEST);
            Mockito.doThrow(SocketTimeoutException.class).when(socket).receive(Mockito.any());
            client.sendCommand(HOST, PORT, request);
        });

        assertTrue(ex.isTimeout());
    }

    @Test
    public void response() throws IOException {
        Mockito.when(request.serialize()).thenReturn(SERIALIZED_REQUEST);
        Mockito.when(request.createResponse(Mockito.any())).thenReturn(response);

        ClientResponse actualResponse = client.sendCommand(HOST, PORT, request);

        ArgumentCaptor<DatagramPacket> datagramPacketCaptor = ArgumentCaptor.forClass(DatagramPacket.class);
        Mockito.verify(socket, Mockito.times(1)).receive(datagramPacketCaptor.capture());
        DatagramPacket sentPacket = datagramPacketCaptor.getValue();

        assertEquals(40, sentPacket.getData().length);
        assertEquals(actualResponse, response);
    }

}