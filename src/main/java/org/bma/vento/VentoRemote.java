package org.bma.vento;

import lombok.extern.slf4j.Slf4j;
import org.bma.vento.client.DefaultVentoClient;
import org.bma.vento.client.RetryableVentoClient;
import org.bma.vento.client.VentoClient;
import org.bma.vento.schedule.ScheduleProperties;
import org.bma.vento.schedule.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;

@Configuration
@Slf4j
@EnableScheduling
public class VentoRemote implements SchedulingConfigurer {
    private static final String SCHEDULE_PROP_FILE = "vento.schedule";

    @Value("${" + SCHEDULE_PROP_FILE + ":classpath:/schedule.yaml}")
    private String schedulePropertiedFileName;

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext("org.bma.vento");
    }

    @Autowired
    private SchedulerService schedulerService;

    @Bean
    public ScheduleProperties scheduleProperties(ResourceLoader resourceLoader) throws IOException {
        log.debug("Loading scheduling properties from: {}", schedulePropertiedFileName);

        ScheduleProperties properties = new Yaml()
                .loadAs(resourceLoader
                                .getResource(schedulePropertiedFileName).getInputStream(),
                        ScheduleProperties.class);

        log.debug("Loaded properties: {}", properties);

        return properties;
    }

    @Bean
    public SchedulerService schedulerService(ScheduleProperties scheduleProperties, VentoClient ventoClient) {
        return new SchedulerService(scheduleProperties, ventoClient);
    }

    @Bean
    public VentoClient ventoClient() {
        return new RetryableVentoClient(new DefaultVentoClient());
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler(); //single threaded by default
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        schedulerService.getScenarioToSchedule().forEach(scenario -> {
            CronTrigger trigger = new CronTrigger(scenario.getCronExp());

            log.info("Scheduling scenario: {}, Next run at: {}", scenario, trigger.nextExecutionTime(new SimpleTriggerContext()));
            taskRegistrar.addCronTask(new CronTask(scenario, trigger));
        });
    }
}
