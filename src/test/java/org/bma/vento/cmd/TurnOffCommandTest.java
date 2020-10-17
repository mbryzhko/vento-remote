package org.bma.vento.cmd;

import org.bma.vento.client.GetSettingsRequest;
import org.bma.vento.client.ShortStatusResponse;
import org.bma.vento.client.TurnOnOffRequest;
import org.bma.vento.client.VentoClient;
import org.bma.vento.schedule.CommandProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TurnOffCommandTest {

    private String HOST = "localhost";

    private TurnOffCommand command;

    @Mock
    private VentoClient ventoClient;

    @Mock
    private ShortStatusResponse getSettingsResponse;

    private CommandProperties properties;

    @BeforeEach
    public void setup() {
        properties = new CommandProperties();
        properties.setHost("localhost");


        command = new TurnOffCommand(ventoClient, properties);
    }

    @Test
    public void getSettingsIsRequestedBeforeTurnOnOffRequest() {
        Mockito.when(getSettingsResponse.isTurnedOn()).thenReturn(false);
        Mockito.when(ventoClient.sendCommand(Mockito.eq(HOST), Mockito.eq(4000), Mockito.isA(GetSettingsRequest.class))).thenReturn(getSettingsResponse);

        command.run();

        Mockito.verify(ventoClient).sendCommand(Mockito.eq(HOST), Mockito.eq(4000), Mockito.isA(GetSettingsRequest.class));
    }

    @Test
    public void sendTurnOnOffRequestIfRemoteHostIsOn() {
        Mockito.when(getSettingsResponse.isTurnedOn()).thenReturn(true);
        Mockito.when(ventoClient.sendCommand(Mockito.eq(HOST), Mockito.eq(4000), Mockito.isA(GetSettingsRequest.class))).thenReturn(getSettingsResponse);

        command.run();

        Mockito.verify(ventoClient).sendCommand(Mockito.eq(HOST), Mockito.eq(4000), Mockito.isA(TurnOnOffRequest.class));
    }

    @Test
    public void dontSentRequestIfRemoteHostIsOff() {
        Mockito.when(getSettingsResponse.isTurnedOn()).thenReturn(false);
        Mockito.when(ventoClient.sendCommand(Mockito.eq(HOST), Mockito.eq(4000), Mockito.isA(GetSettingsRequest.class))).thenReturn(getSettingsResponse);

        command.run();

        Mockito.verify(ventoClient, Mockito.times(0)).sendCommand(Mockito.eq(HOST), Mockito.eq(4000), Mockito.isA(TurnOnOffRequest.class));
    }

}