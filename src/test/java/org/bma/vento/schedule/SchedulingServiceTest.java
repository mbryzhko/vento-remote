package org.bma.vento.schedule;

import org.bma.vento.client.VentoClient;
import org.bma.vento.cmd.TurnOnCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class SchedulingServiceTest {

    private static final String SCENARIO_NAME = "Test Scenario";
    private static final String SCENARIO_CRON_EXP = "0 0/2 * ? * *";
    @Mock
    ScheduleScenarioFactory scheduleScenarioFactory;

    @Mock
    ScheduledTaskRegistrar taskRegistrar;

    @Mock
    VentoClient client;

    @InjectMocks
    SchedulingService service;

    @Test
    public void verifyCronTasksAreRegistered() {
        Mockito.when(scheduleScenarioFactory.getScenarioToSchedule())
                .thenReturn(Collections.singletonList(testScenario()));

        service.configureTasks(taskRegistrar);

        ArgumentCaptor<CronTask> taskCaptor = ArgumentCaptor.forClass(CronTask.class);
        Mockito.verify(taskRegistrar).addCronTask(taskCaptor.capture());

        Assertions.assertEquals(SCENARIO_CRON_EXP, taskCaptor.getValue().getExpression());
        Assertions.assertTrue(taskCaptor.getValue().getRunnable() instanceof ScheduleScenario);
    }

    private ScheduleScenario testScenario() {
        TurnOnCommand command = new TurnOnCommand(client, new CommandProperties());
        return new ScheduleScenario(SCENARIO_NAME, SCENARIO_CRON_EXP, Collections.singletonList(command));
    }
}