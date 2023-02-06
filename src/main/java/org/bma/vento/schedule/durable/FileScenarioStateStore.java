package org.bma.vento.schedule.durable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.util.SerializationUtils;

import java.io.File;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class FileScenarioStateStore implements ScenarioStateStore {

    /**
     * Filename where state of the scenario is stored on Filesystem.
     */
    @NonNull
    private final String schedulingFolderPath;

    @Override
    public ScenarioState readState(String scenarioName) {
        return null;
    }

    @Override
    public void writeState(String scenarioName, ScenarioState state) {

        String scenarioStoreFileName = schedulingFolderPath + File.separator + sanitizeFileName(scenarioName) + ".json";

        try {
            byte[] data = SerializationUtils.serialize(state);

            File storeFile = new File(scenarioStoreFileName);

            FileUtils.writeByteArrayToFile(storeFile, data);
        } catch (IOException e) {
            log.error("Cannot write scenario: {} last execute time. File: {}", scenarioName, scenarioStoreFileName, e);
        }
    }

    private static String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_").toLowerCase();
    }
}
