package org.bma.vento.schedule;

import org.bma.vento.client.VentoClient;
import org.bma.vento.cmd.Command;
import org.bma.vento.cmd.TurnOffCommand;
import org.bma.vento.cmd.TurnOnCommand;
import org.bma.vento.schedule.durable.DurableScheduleScenario;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleScenarioFactory {

    private final ScheduleProperties properties;

    private final VentoClient ventoClient;

    public ScheduleScenarioFactory(ScheduleProperties properties, VentoClient ventoClient) {
        this.properties = properties;
        this.ventoClient = ventoClient;
    }

    public Collection<ScheduleScenario> getScenarioToSchedule() {
        return properties.getScenario()
                .stream()
                .map(this::createScenarioScheduleInfo)
                .collect(Collectors.toList());
    }

    private ScheduleScenario createScenarioScheduleInfo(Scenario scenario) {
        List<Command> commands = createCommandInstances(scenario.getCommands());
        if (properties.getDurabilityProperties().isEnable()) {
            return new DurableScheduleScenario(properties.getDurabilityProperties().getStoreFolderPath(),
                    scenario.getName(), scenario.getCron(), commands);
        } else {
            return new ScheduleScenario(scenario.getName(), scenario.getCron(), commands);
        }
    }

    private List<Command> createCommandInstances(List<CommandProperties> commands) {
        return commands.stream().map(cmdProp -> {
            switch (cmdProp.getType()) {
                case TURN_ON:
                    return new TurnOnCommand(ventoClient, cmdProp);
                case SET_SPEED:
                    return null;
                case TURN_OFF:
                    return new TurnOffCommand(ventoClient, cmdProp);
                default:
                    return null;
            }
        }).collect(Collectors.toList());
    }
}
