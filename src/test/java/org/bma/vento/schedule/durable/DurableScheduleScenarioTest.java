package org.bma.vento.schedule.durable;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.util.SerializationUtils;

import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DurableScheduleScenarioTest {

    private static final String SCENARIO_NAME = "Foo";

    private static final Instant SCENARIO_RUN_DATETIME = Instant.now();

    private DurableScheduleScenario scenario;
    @TempDir
    public File scheduleFolderPath;

    @BeforeEach
    public void setUp() {
        scenario = new DurableScheduleScenario(scheduleFolderPath.getPath(), SCENARIO_NAME, "", Collections.emptyList());
    }

    @Test
    public void shouldWriteDownLastExecutionTimeToFile() throws IOException {
        scenario.setClock(Clock.fixed(SCENARIO_RUN_DATETIME, ZoneId.systemDefault()));

        scenario.run();

        File expectedScenarioStateFile = new File(scenario.getScenarioStoreFileName());
        assertTrue(expectedScenarioStateFile.exists());

        byte[] scenarioRawState = FileUtils.readFileToByteArray(expectedScenarioStateFile);
        ScenarioState scenarioState = (ScenarioState) SerializationUtils.deserialize(scenarioRawState);
        assertEquals(LocalDateTime.ofInstant(SCENARIO_RUN_DATETIME, ZoneId.systemDefault()), scenarioState.getLastExecution());
    }

    @Test
    public void nameOfFileShouldBeSanitized() {
        scenario = new DurableScheduleScenario(scheduleFolderPath.getPath(), "a!b@c#d$e$f%g h", "", Collections.emptyList());

        assertEquals(scheduleFolderPath.getPath() + "/a_b_c_d_e_f_g_h.json", scenario.getScenarioStoreFileName());
    }
}