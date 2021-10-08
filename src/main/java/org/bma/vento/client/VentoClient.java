package org.bma.vento.client;

/**
 * UDP client to ventilation unit.
 */
public interface VentoClient {

    /**
     * Sends a command and recieves response.
     *
     * @param host of ventilation unit
     * @param port of ventilation unit
     * @param request to be sent
     * @return De-serialized response
     */
    <T extends ClientResponse> T sendCommand(String host, int port, ClientRequest<T> request)
            throws VentoClientException;

}
