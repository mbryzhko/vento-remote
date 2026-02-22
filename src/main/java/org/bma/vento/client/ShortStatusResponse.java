package org.bma.vento.client;

import lombok.Getter;
import lombok.ToString;

@ToString
public class ShortStatusResponse extends AbstractClientResponse {

    // Skipped first 6 bytes
    private static final int ON_OFF_STATUS_IDX = 1;
    private static final int SELECTED_SPEED_IDX = 3;

    @Getter
    private boolean turnedOn;

    @Getter
    private int selectedSpeed;

    public ShortStatusResponse(byte[] response) {
        super(response);
    }

    @Override
    protected void parseCommand(byte[] response) {
        turnedOn = response[ON_OFF_STATUS_IDX] == 1;
        selectedSpeed = response[SELECTED_SPEED_IDX];
    }

    // Example of response:
    // 3, 0, 9, 0, 12, 0, 19, 0, 13, 0, 26, 0, 4, 1, 5, 22, 6, 1, 8, 39, 14, 0, 0, 0, 18, 0, 20, 0, 37, 0
}
