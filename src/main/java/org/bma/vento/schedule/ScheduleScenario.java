package org.bma.vento.schedule;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bma.vento.cmd.Command;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Contains details of scenario schedule and commands that should be executed.
 */
@RequiredArgsConstructor
@Getter
@Slf4j
public class ScheduleScenario implements Runnable {

    @NonNull
    private final String name;
    @NonNull
    private final String cronExp;
    private final List<Command> commandsToRun;

    @Override
    public void run() {
        if (CollectionUtils.isEmpty(commandsToRun)) {
            log.warn("Scenario {} has empty list of commands, skipping", name);
        }

        for (Command command : commandsToRun) {
            try {
                command.run();
            } catch (RuntimeException e) {
                log.error("Error running command {} / {}. Error: {}", command.getType(), command.getHost(), e.getMessage(), e);
                break;
            }
        }
    }

    @Override
    public String toString() {
        return name + " (" + cronExp + ")";
    }
}
