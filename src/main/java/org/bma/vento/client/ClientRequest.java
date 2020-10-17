package org.bma.vento.client;

/**
 * Declares request interface.
 *
 * @param <R> type of response.
 * @see ClientResponse
 */
public interface ClientRequest<R extends ClientResponse> {

    /**
     * Serializes request to be sent to remote host.
     *
     * @return serialized request.
     */
    byte[] serialize();

    /**
     * Creates a new instance of response object that can parse row response from remote host.
     *
     * @param response raw response array.
     * @return instance of response object.
     */
    R createResponse(byte[] response);

}
