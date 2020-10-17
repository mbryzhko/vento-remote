package org.bma.vento.schedule;

import org.bma.vento.cmd.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        scenario = ScheduleScenario.builder().commandsToRun(commands).build();
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


}