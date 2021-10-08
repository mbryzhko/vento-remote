package org.bma.vento.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class RetryableVentoClientTest {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8080;

    @Mock
    VentoClient targetClient;

    @Mock
    ClientRequest<ClientResponse> testRequest;

    @Mock
    ClientResponse testResponse;

    @InjectMocks
    RetryableVentoClient retryableClient;

    @Test
    void doNotRetryWhenAllGood() {
        Mockito.when(targetClient.sendCommand(HOST, PORT, testRequest))
                .thenReturn(testResponse);

        ClientResponse resp = retryableClient.sendCommand(HOST, PORT, testRequest);

        Assertions.assertEquals(testResponse, resp);
    }

    @Test
    void doNotRetryWhenTypicalError() {
        Mockito.when(targetClient.sendCommand(HOST, PORT, testRequest))
                .thenThrow(new VentoClientException("Non Timeout exception"));
        Assertions.assertThrows(VentoClientException.class, () -> retryableClient.sendCommand(HOST, PORT, testRequest));
    }

    @Test
    void repeatSendingCommandWhenTimeoutError() {
        VentoClientException ex = new VentoClientException("Timeout exception");
        ex.setTimeout(true);

        Mockito.when(targetClient.sendCommand(HOST, PORT, testRequest))
                .thenThrow(ex) // 1st try timed out
                .thenReturn(testResponse); // 2nd try was successful

        ClientResponse resp = retryableClient.sendCommand(HOST, PORT, testRequest);

        Assertions.assertEquals(testResponse, resp);
    }

    @Test
    void throwTimeoutErrorAfterNumberOfTries() {
        VentoClientException ex = new VentoClientException("Timeout exception");
        ex.setTimeout(true);

        Mockito.when(targetClient.sendCommand(HOST, PORT, testRequest))
                .thenThrow(ex) // 1st try timed out
                .thenThrow(ex) // 2st try timed out
                .thenThrow(ex); // 3rd try timed out

        Assertions.assertThrows(VentoClientException.class, () -> retryableClient.sendCommand(HOST, PORT, testRequest));
        Mockito.verify(targetClient, times(3)).sendCommand(HOST, PORT, testRequest);
    }
}