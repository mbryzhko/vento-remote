package org.bma.vento.schedule;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bma.vento.cmd.Command;

import java.util.List;

@Builder
@Data
@Slf4j
public class ScheduleScenario implements Runnable {

    private final String name;
    private final String cronExp;
    private final List<Command> commandsToRun;

    @Override
    public void run() {
        for (Command command : commandsToRun) {
            try {
                command.run();
            } catch (RuntimeException e) {
                log.error("Error running command {} / {}. Error: {}", command.getType(), command.getHost(), e.getMessage(), e);
                break;
            }
        }
    }
}
