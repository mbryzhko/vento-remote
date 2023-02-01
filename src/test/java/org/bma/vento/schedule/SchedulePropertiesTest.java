package org.bma.vento.schedule;

import org.bma.vento.cmd.CommandType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class SchedulePropertiesTest {

    @Test
    @Disabled
    public void dumpProps() {

        ScheduleProperties properties = new ScheduleProperties();

        Scenario scenario = new Scenario();
        scenario.setName("DOROSLA_ON");
        scenario.setCron("0 0 23 ? * * *");
        properties.getScenario().add(scenario);

        List<CommandProperties> commands = List.of(
                new CommandProperties(CommandType.TURN_ON, "192.168:1.101", 4000, Map.of()),
                new CommandProperties(CommandType.SET_SPEED, "192.168:1.102", 4000, Map.of("value", 1))

        );
        scenario.setCommands(commands);

        Yaml yaml = new Yaml();
        System.out.println(yaml.dump(properties));
    }

    @Test
    public void shouldLoadPropertiesFromFile() {
        InputStream file = SchedulePropertiesTest.class.getResourceAsStream("/test-schedule.yaml");

        ScheduleProperties properties = ScheduleProperties.createFrom(file);

        Assertions.assertEquals(1, properties.getScenario().size());

        Scenario expectedScenario = properties.getScenario().stream().findFirst().get();
        Assertions.assertEquals("Test Scenario", expectedScenario.getName());
        Assertions.assertEquals("0 0/2 * ? * *", expectedScenario.getCron());
        Assertions.assertEquals(1, expectedScenario.getCommands().size());

        CommandProperties expectedCommandProps = expectedScenario.getCommands().get(0);
        Assertions.assertEquals("TURN_ON", expectedCommandProps.getType().name());
        Assertions.assertEquals("192.168.1.101", expectedCommandProps.getHost());
        Assertions.assertEquals(4000, expectedCommandProps.getPort());
        Assertions.assertTrue(expectedCommandProps.getParams().isEmpty());
    }
}
