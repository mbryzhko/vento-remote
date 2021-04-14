package org.bma.vento.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation that reties commend when timeout error.
 */
@AllArgsConstructor
@Slf4j
public class RetryableVentoClient implements VentoClient {
    private static final int MAX_RETRIES_COUNT = 3;

    private final VentoClient target;

    @Override
    public <T extends ClientResponse> T sendCommand(String host, int port, ClientRequest<T> request) {
        int retries = 0;

        while (true) {
            try {
                return target.sendCommand(host, port, request);
            } catch (VentoClientException e) {
                if (e.isTimeout() && ++retries < MAX_RETRIES_COUNT) {
                    log.warn("Timeout sending request: {}", request);
                } else {
                    throw e;
                }
            }
        }
    }
}
