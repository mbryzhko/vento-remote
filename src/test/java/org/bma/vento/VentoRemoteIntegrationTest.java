package org.bma.vento;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;


@ActiveProfiles("test")
@ContextConfiguration(classes = {TestConfig.class, VentoRemote.class})
@TestPropertySource(properties = {"vento.schedule=classpath:/test-schedule.yaml"})
@ExtendWith(SpringExtension.class)
class VentoRemoteIntegrationTest {

    private static final String CRON_EVERY_2_MINUTES = "0 0/2 * ? * *";

    @Autowired
    ScheduledTaskHolder scheduledTaskHolder;

    @Test
    void verifyScenarioScheduledSuccessfully() {
        Set<ScheduledTask> scheduledTasks = scheduledTaskHolder.getScheduledTasks();

        Assertions.assertEquals(1, scheduledTasks.size());
        ScheduledTask task = scheduledTasks.iterator().next();

        Assertions.assertNotNull(task.getTask().getRunnable());
        Assertions.assertTrue(task.getTask() instanceof CronTask);
        Assertions.assertEquals(CRON_EVERY_2_MINUTES, ((CronTask)task.getTask()).getExpression());
    }
}