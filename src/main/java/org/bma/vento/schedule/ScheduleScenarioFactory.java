package org.bma.vento.schedule;

import org.bma.vento.client.VentoClient;
import org.bma.vento.cmd.Command;
import org.bma.vento.cmd.TurnOffCommand;
import org.bma.vento.cmd.TurnOnCommand;
import org.bma.vento.schedule.durable.DurableScheduleScenario;
import org.bma.vento.schedule.durable.ScenarioStateStore;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleScenarioFactory {

    private final ScheduleProperties properties;

    private final VentoClient ventoClient;

    private final ScenarioStateStore scenarioStateStore;

    public ScheduleScenarioFactory(ScheduleProperties properties,
                                   VentoClient ventoClient,
                                   ScenarioStateStore scenarioStateStore) {
        this.properties = properties;
        this.ventoClient = ventoClient;
        this.scenarioStateStore = scenarioStateStore;
    }

    public Collection<ScheduleScenario> getScenarioToSchedule() {
        return properties.getScenario()
                .stream()
                .map(this::createScenarioScheduleInfo)
                .collect(Collectors.toList());
    }

    private ScheduleScenario createScenarioScheduleInfo(Scenario scenario) {
        List<Command> commands = createCommandInstances(scenario.getCommands());
        if (properties.isDurabilityEnabled()) {
            return new DurableScheduleScenario(scenario.getName(), scenario.getCron(), commands, scenarioStateStore);
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
