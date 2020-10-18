package org.bma.vento.schedule;

import org.bma.vento.cmd.CommandType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class SchedulePropertiesTest {

    @Test
    public void dumpProps() {

        ScheduleProperties properties = new ScheduleProperties();

        Scenario scenario = new Scenario();
        scenario.setName("DOROSLA_ON");
        scenario.setCron("0 0 23 ? * * *");
        properties.setScenario(List.of(scenario));


        List<CommandProperties> commands = List.of(
                new CommandProperties(CommandType.TURN_ON, "192.168:1.101", 4000, Map.of()),
                new CommandProperties(CommandType.SET_SPEED, "192.168:1.102", 4000, Map.of("value", 1))

        );
        scenario.setCommands(commands);

        Yaml yaml = new Yaml();
        System.out.println(yaml.dump(properties));
    }

}