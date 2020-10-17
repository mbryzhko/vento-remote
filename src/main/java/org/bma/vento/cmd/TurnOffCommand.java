package org.bma.vento.cmd;

import lombok.extern.slf4j.Slf4j;
import org.bma.vento.client.GetSettingsRequest;
import org.bma.vento.client.ShortStatusResponse;
import org.bma.vento.client.TurnOnOffRequest;
import org.bma.vento.client.VentoClient;
import org.bma.vento.schedule.CommandProperties;

@Slf4j
public class TurnOffCommand extends AbstractCommand {

    public TurnOffCommand(VentoClient client, CommandProperties commandProperties) {
        super(client, commandProperties);
    }

    @Override
    protected void doRun(VentoClient client) {
        ShortStatusResponse initState = client.sendCommand(getHost(), getPort(), new GetSettingsRequest());

        if (initState.isTurnedOn()) {
            log.info("Turning off {}", getHost());
            client.sendCommand(getHost(), getPort(), new TurnOnOffRequest());
        }
    }
}
