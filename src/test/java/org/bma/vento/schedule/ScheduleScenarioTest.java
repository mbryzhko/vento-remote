package org.bma.vento.schedule;

import org.bma.vento.cmd.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ScheduleScenarioTest {

    private ScheduleScenario scenario;

    private List<Command> commands;

    @Mock
    private Command cmd1;

    @Mock
    private Command cmd2;

    @BeforeEach
    public void setup() {
        commands = new ArrayList<>();
        scenario = ScheduleScenario.builder()
                .commandsToRun(commands)
                .cronExp("0 0 23 ? * *")
                .build();
    }

    @Test
    public void commandsAreExecutedInOrder() {
        commands.add(cmd1);
        commands.add(cmd2);

        scenario.run();

        InOrder order = Mockito.inOrder(cmd1, cmd2);
        order.verify(cmd1).run();
        order.verify(cmd2).run();
    }

    @Test
    public void exceptionInterruptsScenario() {
        commands.add(cmd1);
        commands.add(cmd2);

        Mockito.doThrow(RuntimeException.class).when(cmd1).run();

        scenario.run();

        Mockito.verifyZeroInteractions(cmd2);
    }

    @Test
    @Disabled
    public void scheduleTest() throws ParseException {
        CronTrigger trigger = new CronTrigger(scenario.getCronExp());

        LocalDateTime date = LocalDateTime.parse("2022-12-11T16:15:00");

        Date lastExecuted = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());

        Date nextExecutionTime = trigger.nextExecutionTime(new SimpleTriggerContext(lastExecuted, null, null));

        assertEquals(null, nextExecutionTime);
    }
}