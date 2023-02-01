package org.bma.vento;

import org.bma.vento.schedule.ScheduleScenarioFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = VentoRemote.class)
@TestPropertySource(properties = {"vento.schedule=classpath:/test-schedule.yaml"})
@ExtendWith(SpringExtension.class)
class VentoRemoteIntegrationTest {

    @Autowired
    ScheduleScenarioFactory scheduleScenarioFactory;

    @Test
    void test() {
        Assertions.assertNotNull(scheduleScenarioFactory);
    }
}