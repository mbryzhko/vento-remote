package org.bma.vento.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bma.vento.schedule.durable.DurableScheduleScenario;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class SchedulingService implements SchedulingConfigurer {

    private final ScheduleScenarioFactory scheduleScenarioFactory;

    private final ScheduleProperties properties;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        Collection<ScheduleScenario> scenarios = scheduleScenarioFactory.getScenarioToSchedule();

        handleMissedExecution(scenarios);

        scenarios.forEach(scenario -> {
            CronTrigger trigger = new CronTrigger(scenario.getCronExp(), TimeZone.getDefault());

            log.info("Scheduling scenario: {}, Next run at: {}", scenario, trigger.nextExecutionTime(new SimpleTriggerContext()));
            taskRegistrar.addCronTask(new CronTask(scenario, trigger));
        });
    }

    /**
     * If durability is enabled then check if there are missed executions since last time.
     */
    private void handleMissedExecution(Collection<ScheduleScenario> scenarios) {
        if (!properties.isDurabilityEnabled()) return;

        // Keep scenario with missed executions ordered by last execution time
        TreeMap<Date, DurableScheduleScenario> scenarioMissedExecution = new TreeMap<>();

        collectScenarioWithMissedExecutions(scenarios, scenarioMissedExecution);

        while (!scenarioMissedExecution.isEmpty()) {
            Map.Entry<Date, DurableScheduleScenario> entry = scenarioMissedExecution.pollFirstEntry();
            Date lastExecTime = entry.getKey();
            DurableScheduleScenario scenario = entry.getValue();

            Date nextExecutionTime = deriveNextExecutionTime(scenario, lastExecTime);

            if (nextExecutionTime.before(new Date())) {
                log.info("Running missed execution of scenario: {}, run at: {}", scenario.getName(), nextExecutionTime);
                scenario.run();
                scenarioMissedExecution.put(nextExecutionTime, scenario);
            }
        }
    }

    private static void collectScenarioWithMissedExecutions(Collection<ScheduleScenario> allScenarios,
                                                            TreeMap<Date, DurableScheduleScenario> missedExecutions) {
        allScenarios.stream()
                .filter(DurableScheduleScenario.class::isInstance)
                .map(DurableScheduleScenario.class::cast)
                .filter(s -> s.getLastExecutionTime().isPresent())
                .forEach(s -> missedExecutions.put(fromLocalDateTime(s.getLastExecutionTime().get()), s));
    }

    private static Date deriveNextExecutionTime(DurableScheduleScenario s, Date lastExecTime) {
        CronTrigger trigger = new CronTrigger(s.getCronExp(), TimeZone.getDefault());
        return trigger.nextExecutionTime(new SimpleTriggerContext(lastExecTime, lastExecTime, lastExecTime));
    }

    private static Date fromLocalDateTime(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }
}
