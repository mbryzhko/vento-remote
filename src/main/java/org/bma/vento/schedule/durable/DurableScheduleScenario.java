package org.bma.vento.schedule.durable;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bma.vento.cmd.Command;
import org.bma.vento.schedule.ScheduleScenario;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Extends {@code ScheduleScenario} with durability facilities.
 * Each run of the scenario is stored in file.
 */
@Getter
@Slf4j
public class DurableScheduleScenario extends ScheduleScenario {

    @Setter
    private Clock clock = Clock.systemDefaultZone();
    private final ScenarioStateStore scenarioStateStore;

    public DurableScheduleScenario(@NonNull String name, String cron, List<Command> commands,
                                   ScenarioStateStore scenarioStateStore) {
        super(name, cron, commands);

        this.scenarioStateStore = scenarioStateStore;
    }

    @Override
    public void run() {
        super.run();
        writeOutScenarioState();
    }

    public Optional<LocalDateTime> getLastExecutionTime() {
        return Optional.ofNullable(scenarioStateStore.readState(getName())).map(ScenarioState::getLastExecution);
    }

    private void writeOutScenarioState() {
        scenarioStateStore.writeState(getName(), createScenarioState());
    }

    private ScenarioState createScenarioState() {
        return new ScenarioState(LocalDateTime.now(clock));
    }

    @Override
    public String toString() {
        return super.toString() + " Durable";
    }
}
