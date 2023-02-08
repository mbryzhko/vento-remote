package org.bma.vento.schedule;

import org.bma.vento.cmd.Command;
import org.bma.vento.schedule.durable.DurableScheduleScenario;
import org.bma.vento.schedule.durable.ScenarioState;
import org.bma.vento.schedule.durable.ScenarioStateStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class SchedulingServiceTest {

    private static final String SCENARIO_NAME = "Test Scenario";
    private static final String SCENARIO_CRON_EXP_TPL = "%d %d * ? * *";

    ScheduleProperties scheduleProperties = new ScheduleProperties();

    @Mock
    ScheduleScenarioFactory scheduleScenarioFactory;

    @Mock
    ScheduledTaskRegistrar taskRegistrar;

    @Mock
    Command testCommand;

    @Mock
    Command testCommand1;

    @Mock
    ScenarioStateStore store;

    SchedulingService service;

    @BeforeEach
    public void setup() {
        service = new SchedulingService(scheduleScenarioFactory, scheduleProperties);
    }

    @Test
    public void verifyCronTasksAreRegistered() {
        givenScheduledScenario(simpleScenario(), durableScenario());

        service.configureTasks(taskRegistrar);

        ArgumentCaptor<CronTask> taskCaptor = ArgumentCaptor.forClass(CronTask.class);
        Mockito.verify(taskRegistrar, Mockito.times(2)).addCronTask(taskCaptor.capture());

        Assertions.assertNotNull(taskCaptor.getValue().getExpression());
        Assertions.assertTrue(taskCaptor.getValue().getRunnable() instanceof ScheduleScenario);
    }

    @Test
    public void shouldTriggerMissedScenarioRunWhenDurable() {
        scheduleProperties.getDurabilityProperties().setEnable(true);

        ScheduleScenario s = durableScenario();
        ScheduleScenario s2 = durableScenario1();
        givenScheduledScenario(s, s2);

        Mockito.when(store.readState(Mockito.eq(s.getName())))
                .thenReturn(new ScenarioState(LocalDateTime.now().minusHours(2)));

        Mockito.when(store.readState(Mockito.eq(s2.getName())))
                .thenReturn(new ScenarioState(LocalDateTime.now().minusHours(1)));

        service.configureTasks(taskRegistrar);

        Mockito.verify(testCommand, Mockito.times(2)).run();
        Mockito.verify(testCommand1).run();
    }

    @Test
    public void shouldNotHandleMissingRunsWhenNoLastExecutionTime() {
        scheduleProperties.getDurabilityProperties().setEnable(true);

        givenScheduledScenario(durableScenario());

        Mockito.when(store.readState(Mockito.eq(SCENARIO_NAME))).thenReturn(null);

        service.configureTasks(taskRegistrar);

        Mockito.verify(testCommand, Mockito.times(0)).run();
    }

    @Test
    public void shouldNotHandleMissingRunsWhenDurabilityIsNotEnabled() {
        scheduleProperties.getDurabilityProperties().setEnable(false);

        givenScheduledScenario(simpleScenario());

        service.configureTasks(taskRegistrar);

        Mockito.verify(testCommand, Mockito.times(0)).run();
        Mockito.verifyZeroInteractions(store);
    }

    private ScheduleScenario simpleScenario() {
        return new ScheduleScenario(SCENARIO_NAME, createSchedulingExpression(), Collections.singletonList(testCommand));
    }

    private ScheduleScenario durableScenario() {
        return new DurableScheduleScenario(SCENARIO_NAME, createSchedulingExpression(), Collections.singletonList(testCommand), store);
    }

    private ScheduleScenario durableScenario1() {
        return new DurableScheduleScenario("Scenario 1", createSchedulingExpression(), Collections.singletonList(testCommand1), store);
    }

    private void givenScheduledScenario(ScheduleScenario... scenario) {
        Mockito.when(scheduleScenarioFactory.getScenarioToSchedule()).thenReturn(Arrays.asList(scenario));
    }

    private String createSchedulingExpression() {
        LocalDateTime nextSchedulingDate = LocalDateTime.now().plusHours(1);
        return String.format(SCENARIO_CRON_EXP_TPL, nextSchedulingDate.getSecond(), nextSchedulingDate.getMinute());
    }
}