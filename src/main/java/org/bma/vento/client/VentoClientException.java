package org.bma.vento.client;

/**
 * Any errors that happens inside {@code VentoClient}.
 */
public class VentoClientException extends RuntimeException {

    public VentoClientException(String message) {
        super(message);
    }

    public VentoClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
