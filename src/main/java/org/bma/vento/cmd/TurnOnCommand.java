package org.bma.vento.cmd;

import lombok.extern.slf4j.Slf4j;
import org.bma.vento.client.GetSettingsRequest;
import org.bma.vento.client.SetSpeedRequest;
import org.bma.vento.client.ShortStatusResponse;
import org.bma.vento.client.TurnOnOffRequest;
import org.bma.vento.client.VentoClient;
import org.bma.vento.schedule.CommandProperties;

@Slf4j
public class TurnOnCommand extends AbstractCommand {

    private static final int DEFAULT_SPEED = 1;

    public TurnOnCommand(VentoClient client, CommandProperties commandProperties) {
        super(client, commandProperties);
    }

    @Override
    protected void doRun(VentoClient client) {
        ShortStatusResponse initState = client.sendCommand(getHost(), getPort(), new GetSettingsRequest());

        if (!initState.isTurnedOn()) {
            log.info("Turning on {}", getHost());
            client.sendCommand(getHost(), getPort(), new TurnOnOffRequest());
        }

        int speed = (int) getParams().getOrDefault("speed", DEFAULT_SPEED);
        if (initState.getSelectedSpeed() != speed) {
            log.info("Setting speed {} on {}", speed, getHost());
            client.sendCommand(getHost(), getPort(), new SetSpeedRequest(speed));
        }
    }
}
