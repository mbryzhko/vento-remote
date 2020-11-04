package org.bma.vento.cmd;

import org.bma.vento.client.GetSettingsRequest;
import org.bma.vento.client.TurnOnOffRequest;
import org.bma.vento.client.VentoClient;
import org.bma.vento.cmd.TurnOffCommand;
import org.bma.vento.cmd.TurnOnCommand;
import org.bma.vento.schedule.CommandProperties;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

public class CommandRunner {

    public static void main(String [] args) throws IOException {
        VentoClient client = new VentoClient();
        CommandProperties properties = new CommandProperties();
        properties.setHost("192.168.1.101");
//        client.sendCommand("192.168.1.101", 4000, new GetSettingsRequest());
//        client.sendCommand("192.168.1.102", 4000, new GetSettingsRequest());

//        new TurnOnCommand(client, properties).run();
        new TurnOffCommand(client, properties).run();
    }

    // [109, 97, 115, 116, 101, 114, 3, 0, 9, 0, 12, 0, 19, 0, 13, 0, 26, 0, 4, 1, 5, -1, 6, 1, 8, 56, 14, 0, 0, 0, 18, 0, 20, 0, 37, 0, 0, 0, 0, 0]
    // [109, 97, 115, 116, 101, 114, 3, 0, 9, 0, 12, 0, 19, 0, 13, 0, 26, 0, 4, 1, 5, -1, 6, 1, 8, 48, 14, 0, 0, 0, 18, 0, 20, 0, 37, 0, 0, 0, 0, 0]
}
