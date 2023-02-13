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
    public ScenarioState readState(@NonNull String scenarioName) {
        String scenarioStoreFileName = getScenarioStoreFileName(scenarioName);

        try {
            File storeFile = new File(scenarioStoreFileName);

            if (!storeFile.exists()) {
                log.warn("State of scenario not found: {}, file: {}", scenarioName, scenarioStoreFileName);
                return null;
            }

            byte[] rawState = FileUtils.readFileToByteArray(storeFile);
            return (ScenarioState) SerializationUtils.deserialize(rawState);
        } catch (IOException | RuntimeException e) {
            log.error("Cannot read state of scenario: {}, file: {}, message: {}", scenarioName, scenarioStoreFileName, e.getMessage());
            return null;
        }
    }

    @Override
    public void writeState(@NonNull String scenarioName, @NonNull ScenarioState state) {
        String scenarioStoreFileName = getScenarioStoreFileName(scenarioName);

        log.debug("Writing state of scenario: {}, state: {}, file: {}", scenarioName, state, scenarioStoreFileName);

        try {
            File storeFile = new File(scenarioStoreFileName);

            byte[] rawState = SerializationUtils.serialize(state);
            FileUtils.writeByteArrayToFile(storeFile, rawState);
        } catch (IOException | RuntimeException e) {
            log.error("Cannot write state of scenario: {}, file: {}", scenarioName, scenarioStoreFileName, e);
        }
    }

    private String getScenarioStoreFileName(String scenarioName) {
        return schedulingFolderPath + File.separator + sanitizeFileName(scenarioName) + ".json";
    }

    private static String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_").toLowerCase();
    }
}
