package org.bma.vento.client;

import lombok.Getter;
import lombok.Setter;

/**
 * Any errors that happens inside {@code VentoClient}.
 */
public class VentoClientException extends RuntimeException {

    @Setter @Getter
    private boolean timeout = false;

    public VentoClientException(String message) {
        super(message);
    }

    public VentoClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
