package org.bma.vento.schedule.durable;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.bma.vento.cmd.Command;
import org.bma.vento.schedule.ScheduleScenario;
import org.springframework.util.SerializationUtils;

import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Extends {@code ScheduleScenario} with durability facilities.
 * Each run of the scenario is stored in file.
 */
@Getter
@Slf4j
public class DurableScheduleScenario extends ScheduleScenario {

    /**
     * Filename where state of the scenario is stored on Filesystem.
     */
    private final String scenarioStoreFileName;

    @Setter
    private Clock clock = Clock.systemDefaultZone();

    public DurableScheduleScenario(@NonNull String schedulingFolderPath,
                                   @NonNull String name, String cron, List<Command> commands) {
        super(name, cron, commands);

        this.scenarioStoreFileName = schedulingFolderPath + File.separator + sanitizeFileName(name) + ".json";
    }

    private static String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_").toLowerCase();
    }

    @Override
    public void run() {
        super.run();
        writeOutScenarioState();
    }

    private void writeOutScenarioState() {
        try {
            byte[] data = SerializationUtils.serialize(createScenarioState());

            File storeFile = new File(scenarioStoreFileName);

            FileUtils.writeByteArrayToFile(storeFile, data);
        } catch (IOException e) {
            log.error("Cannot write scenario: {} last execute time. File: {}", getName(), scenarioStoreFileName, e);
        }
    }

    private ScenarioState createScenarioState() {
        return new ScenarioState(LocalDateTime.now(clock));
    }
}
