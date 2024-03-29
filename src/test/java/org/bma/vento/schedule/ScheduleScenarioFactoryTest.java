package org.bma.vento.schedule;

import org.bma.vento.client.DefaultVentoClient;
import org.bma.vento.cmd.CommandType;
import org.bma.vento.cmd.TurnOnCommand;
import org.bma.vento.schedule.durable.DurableScheduleScenario;
import org.bma.vento.schedule.durable.ScenarioStateStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ScheduleScenarioFactoryTest {

    public static final String TURN_ON_NAME = "TurnOn";
    public static final String REMOTE_HOST = "localost";
    public static final String CRON_EXP = "* * *";
    public static final String FOLDER_PATH = "/foo/path";

    private ScheduleScenarioFactory service;

    private ScheduleProperties properties;

    @Mock
    private DefaultVentoClient ventoClient;

    @Mock
    private ScenarioStateStore store;

    @BeforeEach
    public void setup() {
        properties = new ScheduleProperties();

        service = new ScheduleScenarioFactory(properties, ventoClient, store);
    }

    @Test
    public void creatingListOfScheduledScenario() {
        properties.getScenario().add(turnOnScenario());

        Collection<ScheduleScenario> toSchedule = service.getScenarioToSchedule();

        assertEquals(1, toSchedule.size());
        ScheduleScenario scheduleScenario = toSchedule.stream().findFirst().get();
        assertEquals(CRON_EXP, scheduleScenario.getCronExp());
        assertEquals(TURN_ON_NAME, scheduleScenario.getName());
        assertEquals(1, scheduleScenario.getCommandsToRun().size());
        assertEquals(TurnOnCommand.class, scheduleScenario.getCommandsToRun().get(0).getClass());
    }

    @Test
    public void shouldCreateDurableScheduleScenarioWhenItSetInProperties() {
        properties.getScenario().add(turnOnScenario());
        properties.getDurability().setEnable(true);
        properties.getDurability().setStoreFolderPath(FOLDER_PATH);

        Collection<ScheduleScenario> toSchedule = service.getScenarioToSchedule();

        ScheduleScenario durableScenario = toSchedule.stream().findFirst().get();
        assertTrue(durableScenario instanceof DurableScheduleScenario);
    }

    private Scenario turnOnScenario() {
        return new Scenario(TURN_ON_NAME, CRON_EXP, Collections.singletonList(new CommandProperties(CommandType.TURN_ON,
                REMOTE_HOST, 4000, Collections.emptyMap())));
    }
}
