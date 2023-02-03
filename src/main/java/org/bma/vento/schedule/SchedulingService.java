package org.bma.vento.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.util.TimeZone;

@Slf4j
@RequiredArgsConstructor
public class SchedulingService implements SchedulingConfigurer {

    private final ScheduleScenarioFactory scheduleScenarioFactory;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        scheduleScenarioFactory.getScenarioToSchedule().forEach(scenario -> {
            CronTrigger trigger = new CronTrigger(scenario.getCronExp(), TimeZone.getDefault());

            log.info("Scheduling scenario: {}, Next run at: {}", scenario, trigger.nextExecutionTime(new SimpleTriggerContext()));
            taskRegistrar.addCronTask(new CronTask(scenario, trigger));
        });
    }
}
